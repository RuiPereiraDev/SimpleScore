package com.r4g3baby.simplescore.bukkit.protocol.legacy

import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector
import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector.Companion.writePacket
import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector.Companion.writePackets
import com.r4g3baby.simplescore.bukkit.protocol.ProtocolHandler
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedDisplayObjective
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedDisplayObjective.Position
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedUpdateObjective
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedUpdateObjective.Mode
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedUpdateObjective.Type
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedUpdateScore
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedUpdateScore.Action
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedUpdateTeam
import com.r4g3baby.simplescore.bukkit.protocol.legacy.packet.WrappedUpdateTeam.TeamMode
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveScore
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveTitle
import com.r4g3baby.simplescore.bukkit.protocol.model.PlayerObjective
import org.bukkit.entity.Player

open class LegacyProtocolHandler : ProtocolHandler() {
    override fun createObjective(player: Player, title: ObjectiveTitle): PlayerObjective {
        return playerObjectives.computeIfAbsent(player.uniqueId) {
            with(ChannelInjector.getChannel(player)) {
                val objectiveName = getObjectiveName(player)
                val titleText = title.text.take(32)
                writePackets(
                    WrappedUpdateObjective(objectiveName, Mode.CREATE, Type.INTEGER, titleText),
                    WrappedDisplayObjective(objectiveName, Position.SIDEBAR)
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
                val titleText = title.text.take(32)
                writePacket(WrappedUpdateObjective(objectiveName, Mode.UPDATE, Type.INTEGER, titleText))
            }

            val objectiveScores = mutableMapOf<String, ObjectiveScore>()
            scores.forEach { (identifier, newScore) ->
                val scoreName = identifierToName(identifier)
                val (_, text, score, _) = newScore

                val currentScore = playerObjective.scores[identifier]
                if (currentScore != null) {
                    if (currentScore.text != text) {
                        val (prefix, suffix) = splitScoreLine(text, true)
                        writePacket(WrappedUpdateTeam(scoreName, TeamMode.UPDATE, "", prefix, suffix))
                    }

                    if (score != currentScore.value) {
                        writePacket(WrappedUpdateScore(objectiveName, scoreName, Action.UPDATE, score))
                    }

                    objectiveScores[identifier] = newScore
                    return@forEach
                }

                val (prefix, suffix) = splitScoreLine(text, true)

                writePackets(
                    WrappedUpdateTeam(scoreName, TeamMode.CREATE, "", prefix, suffix, listOf(scoreName)),
                    WrappedUpdateScore(objectiveName, scoreName, Action.UPDATE, score)
                )

                objectiveScores[identifier] = newScore
            }

            playerObjective.scores.forEach { (identifier, _) ->
                if (scores.containsKey(identifier)) return@forEach

                val scoreName = identifierToName(identifier)
                writePackets(
                    WrappedUpdateScore(objectiveName, scoreName, Action.REMOVE),
                    WrappedUpdateTeam(scoreName, TeamMode.REMOVE)
                )
            }

            playerObjectives.computeIfPresent(player.uniqueId) { _, _ ->
                PlayerObjective(title, objectiveScores)
            }
        }
    }
}