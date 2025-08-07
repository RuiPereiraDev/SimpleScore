package com.r4g3baby.simplescore.bukkit.protocol.modern

import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector
import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector.Companion.writePacket
import com.r4g3baby.simplescore.bukkit.protocol.ChannelInjector.Companion.writePackets
import com.r4g3baby.simplescore.bukkit.protocol.ProtocolHandler
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveScore
import com.r4g3baby.simplescore.bukkit.protocol.model.PlayerObjective
import com.r4g3baby.simplescore.bukkit.protocol.model.ScoreData
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
    override fun createObjective(player: Player, title: String?): PlayerObjective {
        return playerObjectives.computeIfAbsent(player.uniqueId) {
            with(ChannelInjector.getChannel(player)) {
                val objectiveName = getObjectiveName(player)
                writePackets(
                    WrappedUpdateObjective(objectiveName, Mode.CREATE, Type.INTEGER, fromString(title ?: "")),
                    WrappedDisplayObjective(objectiveName, Position.SIDEBAR)
                )
            }

            return@computeIfAbsent PlayerObjective(title, emptyList())
        }
    }

    override fun removeObjective(player: Player): PlayerObjective? {
        val playerObjective = playerObjectives.remove(player.uniqueId) ?: return null
        with(ChannelInjector.getChannel(player)) {
            writePacket(WrappedUpdateObjective(getObjectiveName(player), Mode.REMOVE))
        }
        return playerObjective
    }

    override fun updateScoreboard(player: Player, title: String?, scores: List<ScoreData>) {
        val playerObjective = playerObjectives[player.uniqueId] ?: return
        with(ChannelInjector.getChannel(player)) {
            val objectiveName = getObjectiveName(player)

            if (title != null && playerObjective.title != title) {
                writePacket(WrappedUpdateObjective(objectiveName, Mode.UPDATE, Type.INTEGER, fromString(title)))
            }

            val objectiveScores = mutableListOf<ObjectiveScore>()
            scores.forEach { (identifier, text, score, hideNumber) ->
                val currentScore = playerObjective.scores.find { it.identifier == identifier }
                if (currentScore?.text == text && currentScore?.score == score && currentScore.hideNumber == hideNumber) {
                    objectiveScores.add(currentScore)
                    return@forEach
                }

                val displayText = text ?: currentScore?.text ?: return@forEach
                val displayName = fromString(displayText)

                val numberFormat = if (hideNumber) WrappedNumberFormat.blankFormat else null

                writePacket(
                    WrappedUpdateScore(identifier, objectiveName, Action.UPDATE, score, displayName, numberFormat)
                )
                objectiveScores.add(ObjectiveScore(identifier, displayText, score, hideNumber))
            }

            playerObjective.scores.forEach { (identifier, _, _) ->
                if (scores.any { it.identifier == identifier }) return@forEach
                writePacket(WrappedResetScore(identifier, objectiveName))
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