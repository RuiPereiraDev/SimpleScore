package com.r4g3baby.simplescore.bukkit.scoreboard

import com.r4g3baby.simplescore.bukkit.BukkitManager
import com.r4g3baby.simplescore.bukkit.protocol.ProtocolHandler
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveScore
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveTitle

class ScoreboardTask(
    private val manager: BukkitManager,
    private val protocolHandler: ProtocolHandler
) : Runnable {
    private val emptyTitle = ObjectiveTitle("-1", "")

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

            val title = scoreboard.getTitle(player, manager.varReplacer).let { line ->
                if (line == null) return@let emptyTitle

                val currentTitle = playerObjective?.title
                if (currentTitle == null || currentTitle.lineIdentifier != line.identifier || line.shouldRender()) {
                    ObjectiveTitle(line.identifier, line.currentText(player, manager.varReplacer))
                } else currentTitle
            }

            if (playerObjective == null) playerObjective = protocolHandler.createObjective(player, title)

            val scores = mutableMapOf<String, ObjectiveScore>()
            scoreboard.getScores(player, manager.varReplacer).forEach scoresForEach@{ scoreboardScore ->
                val value = scoreboardScore.getValueAsInteger(player, manager.varReplacer) ?: return@scoresForEach
                val line = scoreboardScore.getLine(player, manager.varReplacer) ?: return@scoresForEach

                val currentScore = playerObjective.scores[scoreboardScore.identifier]
                val text = if (currentScore == null || currentScore.lineIdentifier != line.identifier || line.shouldRender()) {
                    line.currentText(player, manager.varReplacer)
                } else currentScore.text

                scores[scoreboardScore.identifier] = ObjectiveScore(line.identifier, text, value, scoreboardScore.hideNumber)
            }

            protocolHandler.updateScoreboard(player, title, scores)
        }
    }
}