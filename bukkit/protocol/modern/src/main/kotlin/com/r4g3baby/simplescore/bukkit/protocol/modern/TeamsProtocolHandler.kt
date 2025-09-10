package com.r4g3baby.simplescore.bukkit.protocol.modern

import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector
import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector.Companion.writePacket
import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector.Companion.writePackets
import com.r4g3baby.simplescore.bukkit.protocol.ProtocolHandler
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveScore
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveTitle
import com.r4g3baby.simplescore.bukkit.protocol.model.PlayerObjective
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.WrappedChatComponent
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.WrappedChatComponent.Companion.fromString
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedDisplayObjective
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateObjective
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateObjective.Mode
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateObjective.Type
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateScore
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateScore.Action
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateTeam
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateTeam.TeamMode
import org.bukkit.entity.Player

class TeamsProtocolHandler : ProtocolHandler() {
    private val emptyComponent = fromString("")

    override fun createObjective(player: Player, title: ObjectiveTitle): PlayerObjective {
        return playerObjectives.computeIfAbsent(player.uniqueId) {
            with(ChannelInjector.getChannel(player)) {
                val objectiveName = getObjectiveName(player)
                writePackets(
                    WrappedUpdateObjective(objectiveName, Mode.CREATE, Type.INTEGER, fromString(title.text)),
                    WrappedDisplayObjective(objectiveName, WrappedDisplayObjective.Position.SIDEBAR)
                )
            }

            return@computeIfAbsent PlayerObjective(title, emptyMap())
        }
    }

    override fun removeObjective(player: Player): PlayerObjective? {
        val playerObjective = playerObjectives.remove(player.uniqueId) ?: return null

        with(ChannelInjector.getChannel(player)) {
            val objectiveName = getObjectiveName(player)
            writePacket(WrappedUpdateObjective(objectiveName, Mode.REMOVE))

            playerObjective.scores.forEach { (identifier, _) ->
                writePacket(WrappedUpdateTeam(identifierToName(identifier), TeamMode.REMOVE))
            }
        }

        return playerObjective
    }

    override fun updateScoreboard(player: Player, title: ObjectiveTitle, scores: Map<String, ObjectiveScore>) {
        val playerObjective = playerObjectives[player.uniqueId] ?: return
        with(ChannelInjector.getChannel(player)) {
            val objectiveName = getObjectiveName(player)

            if (playerObjective.title != title) {
                writePacket(WrappedUpdateObjective(objectiveName, Mode.UPDATE, Type.INTEGER, fromString(title.text)))
            }

            val objectiveScores = mutableMapOf<String, ObjectiveScore>()
            scores.forEach { (identifier, newScore) ->
                val scoreName = identifierToName(identifier)
                val (_, text, value, _) = newScore

                val currentScore = playerObjective.scores[identifier]
                if (currentScore != null) {
                    if (currentScore.text != text) {
                        val (prefix, suffix) = parseLine(text)
                        writePacket(WrappedUpdateTeam(scoreName, TeamMode.UPDATE, emptyComponent, prefix, suffix))
                    }

                    if (value != currentScore.value) {
                        writePacket(WrappedUpdateScore(scoreName, objectiveName, Action.UPDATE, value))
                    }

                    objectiveScores[identifier] = newScore
                    return@forEach
                }

                val (prefix, suffix) = parseLine(text)
                writePackets(
                    WrappedUpdateTeam(
                        scoreName, TeamMode.CREATE, emptyComponent, prefix, suffix, listOf(scoreName)
                    ),
                    WrappedUpdateScore(scoreName, objectiveName, Action.UPDATE, value)
                )

                objectiveScores[identifier] = newScore
            }

            playerObjective.scores.forEach { (identifier, _) ->
                if (scores.containsKey(identifier)) return@forEach

                val scoreName = identifierToName(identifier)
                writePackets(
                    WrappedUpdateScore(scoreName, objectiveName, Action.REMOVE),
                    WrappedUpdateTeam(scoreName, TeamMode.REMOVE)
                )
            }

            playerObjectives.computeIfPresent(player.uniqueId) { _, _ ->
                PlayerObjective(title, objectiveScores)
            }
        }
    }

    private fun parseLine(line: String): Pair<WrappedChatComponent, WrappedChatComponent> {
        if (line.isEmpty()) return emptyComponent to emptyComponent

        val (prefix, suffix) = splitScoreLine(line)
        return fromString(prefix) to fromString(suffix)
    }
}