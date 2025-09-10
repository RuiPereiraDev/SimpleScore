package com.r4g3baby.simplescore.bukkit.protocol.modern

import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector
import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector.Companion.writePacket
import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector.Companion.writePackets
import com.r4g3baby.simplescore.bukkit.protocol.ProtocolHandler
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveScore
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveTitle
import com.r4g3baby.simplescore.bukkit.protocol.model.PlayerObjective
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.WrappedChatComponent.Companion.fromString
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.numbers.WrappedNumberFormat
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedDisplayObjective
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedDisplayObjective.Position
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedResetScore
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateObjective
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateObjective.Mode
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateObjective.Type
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateScore
import com.r4g3baby.simplescore.bukkit.protocol.modern.packet.WrappedUpdateScore.Action
import org.bukkit.entity.Player

class ModernProtocolHandler : ProtocolHandler() {
    override fun createObjective(player: Player, title: ObjectiveTitle): PlayerObjective {
        return playerObjectives.computeIfAbsent(player.uniqueId) {
            with(ChannelInjector.getChannel(player)) {
                val objectiveName = getObjectiveName(player)
                writePackets(
                    WrappedUpdateObjective(objectiveName, Mode.CREATE, Type.INTEGER, fromString(title.text)),
                    WrappedDisplayObjective(objectiveName, Position.SIDEBAR)
                )
            }

            return@computeIfAbsent PlayerObjective(title, emptyMap())
        }
    }

    override fun removeObjective(player: Player): PlayerObjective? {
        val playerObjective = playerObjectives.remove(player.uniqueId) ?: return null
        with(ChannelInjector.getChannel(player)) {
            writePacket(WrappedUpdateObjective(getObjectiveName(player), Mode.REMOVE))
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
                val (_, text, value, hideNumber) = newScore

                val currentScore = playerObjective.scores[identifier]
                if (currentScore == newScore) {
                    objectiveScores[identifier] = currentScore
                    return@forEach
                }

                val displayName = fromString(text)
                val numberFormat = if (hideNumber) WrappedNumberFormat.blankFormat else null

                writePacket(
                    WrappedUpdateScore(identifier, objectiveName, Action.UPDATE, value, displayName, numberFormat)
                )
                objectiveScores[identifier] = newScore
            }

            playerObjective.scores.forEach { (identifier, _) ->
                if (scores.containsKey(identifier)) return@forEach
                writePacket(WrappedResetScore(identifier, objectiveName))
            }

            playerObjectives.computeIfPresent(player.uniqueId) { _, _ ->
                PlayerObjective(title, objectiveScores)
            }
        }
    }
}