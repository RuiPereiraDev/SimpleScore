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
import com.r4g3baby.simplescore.bukkit.protocol.model.PlayerObjective
import com.r4g3baby.simplescore.bukkit.protocol.model.ScoreData
import org.bukkit.entity.Player

open class LegacyProtocolHandler : ProtocolHandler() {
    override fun createObjective(player: Player, title: String?): PlayerObjective {
        return playerObjectives.computeIfAbsent(player.uniqueId) {
            with(ChannelInjector.getChannel(player)) {
                val objectiveName = getObjectiveName(player)
                writePackets(
                    WrappedUpdateObjective(objectiveName, Mode.CREATE, Type.INTEGER, title ?: ""),
                    WrappedDisplayObjective(objectiveName, Position.SIDEBAR)
                )
            }

            return@computeIfAbsent PlayerObjective(title, emptyList())
        }
    }

    override fun removeObjective(player: Player): PlayerObjective? {
        val playerObjective = playerObjectives.remove(player.uniqueId) ?: return null

        with(ChannelInjector.getChannel(player)) {
            val objectiveName = getObjectiveName(player)
            writePacket(WrappedUpdateObjective(objectiveName, Mode.REMOVE))

            playerObjective.scores.forEach { (identifier, _, _) ->
                writePacket(WrappedUpdateTeam(identifierToName(identifier), TeamMode.REMOVE))
            }
        }

        return playerObjective
    }

    override fun updateScoreboard(player: Player, title: String?, scores: List<ScoreData>) {
        val playerObjective = playerObjectives[player.uniqueId] ?: return
        with(ChannelInjector.getChannel(player)) {
            val objectiveName = getObjectiveName(player)

            if (title != null && playerObjective.title != title) {
                writePacket(WrappedUpdateObjective(objectiveName, Mode.UPDATE, Type.INTEGER, title))
            }

            val objectiveScores = mutableListOf<ObjectiveScore>()
            scores.forEach { (identifier, text, score, hideNumber) ->
                val scoreName = identifierToName(identifier)

                val currentScore = playerObjective.scores.find { it.identifier == identifier }
                if (currentScore != null) {
                    if (text != null && currentScore.text != text) {
                        val (prefix, suffix) = splitScoreLine(text, true)
                        writePacket(WrappedUpdateTeam(scoreName, TeamMode.UPDATE, "", prefix, suffix))
                    }

                    if (score != currentScore.score) {
                        writePacket(WrappedUpdateScore(objectiveName, scoreName, Action.UPDATE, score))
                    }

                    objectiveScores.add(ObjectiveScore(identifier, text ?: currentScore.text, score, hideNumber))
                    return@forEach
                }

                if (text == null) return@forEach
                val (prefix, suffix) = splitScoreLine(text, true)

                writePackets(
                    WrappedUpdateTeam(scoreName, TeamMode.CREATE, "", prefix, suffix, listOf(scoreName)),
                    WrappedUpdateScore(objectiveName, scoreName, Action.UPDATE, score)
                )

                objectiveScores.add(ObjectiveScore(identifier, text, score, hideNumber))
            }

            playerObjective.scores.forEach { (identifier, _, _) ->
                if (scores.any { it.identifier == identifier }) return@forEach

                val scoreName = identifierToName(identifier)
                writePackets(
                    WrappedUpdateScore(objectiveName, scoreName, Action.REMOVE),
                    WrappedUpdateTeam(scoreName, TeamMode.REMOVE)
                )
            }

            playerObjectives.computeIfPresent(player.uniqueId) {_, _ ->
                PlayerObjective(title ?: playerObjective.title, objectiveScores)
            }
            /*playerObjective.apply {
                this.title = title ?: this.title
                this.scores = objectiveScores
            }*/
        }
    }
}