package com.r4g3baby.simplescore.bukkit.scoreboard

import com.r4g3baby.simplescore.bukkit.BukkitManager
import com.r4g3baby.simplescore.bukkit.protocol.ProtocolHandler
import com.r4g3baby.simplescore.bukkit.protocol.model.ScoreData

class ScoreboardTask(
    private val manager: BukkitManager,
    private val protocolHandler: ProtocolHandler
) : Runnable {
    override fun run() {
        manager.scoreboards.forEach { scoreboard ->
            scoreboard.tick()
        }

        manager.viewers.forEach { viewer ->
            val player = viewer.reference.get()
            if (player == null || !player.isOnline) return@forEach

            val scoreboard = if (!viewer.isScoreboardHidden) viewer.scoreboard else null
            var playerObjective = protocolHandler.getObjective(player)
            if (scoreboard == null || !scoreboard.canSee(player, manager.varReplacer)) {
                if (playerObjective != null) protocolHandler.removeObjective(player)
                return@forEach
            }

            val hasTitle = playerObjective?.title != null
            val title = scoreboard.getTitle(player, manager.varReplacer)?.let { title ->
                if (!hasTitle || title.shouldRender()) title.currentText(player, manager.varReplacer) else null
            }

            if (playerObjective == null) playerObjective = protocolHandler.createObjective(player, title)

            val scores = mutableListOf<ScoreData>()
            scoreboard.getScores(player, manager.varReplacer).forEach scoresForEach@{ scoreboardScore ->
                val score = scoreboardScore.getScoreAsInteger(player, manager.varReplacer) ?: return@scoresForEach
                val line = scoreboardScore.getLine(player, manager.varReplacer) ?: return@scoresForEach

                val hasScore = playerObjective.scores.any { it.identifier == scoreboardScore.identifier }
                val text = if (!hasScore || line.shouldRender()) line.currentText(player, manager.varReplacer) else null

                val hideNumber = scoreboard.hideNumbers || scoreboardScore.hideNumber
                scores.add(ScoreData(scoreboardScore.identifier, text, score, hideNumber))
            }

            protocolHandler.updateScoreboard(player, title, scores)
        }
    }
}