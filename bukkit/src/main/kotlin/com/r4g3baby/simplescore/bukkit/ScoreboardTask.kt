package com.r4g3baby.simplescore.bukkit

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.Scoreboard
import com.r4g3baby.simplescore.ScoreboardScore
import com.r4g3baby.simplescore.api.VarReplacer
import com.r4g3baby.simplescore.lines.StaticLine
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class ScoreboardTask(private val plugin: BukkitPlugin) : BukkitRunnable() {
    val scoreboards: List<Scoreboard<Player>> = listOf(
        Scoreboard(
            "myScoreboardName", listOf(
                StaticLine("Hello World!")
            ), listOf(
                ScoreboardScore(1, StaticLine("%uuid%"))
            )
        )
    )
    private val replacer = VarReplacer<Player> { text, viewer ->
        text.replace("%uuid%", viewer.uniqueId.toString())
    }

    override fun run() {
        scoreboards.forEach { scoreboard ->
            scoreboard.tick()

            scoreboard.getViewers().forEach viewersForEach@{ viewer ->
                if (!viewer.isOnline) return@viewersForEach

                val title = scoreboard.getTitle(viewer)?.let { title ->
                    if (title.shouldRender()) title.currentText(viewer, replacer) else null
                }

                val scores = HashMap<Int, String?>()
                scoreboard.getScores(viewer).forEach scoresForEach@{ scoreboardScore ->
                    val score = scoreboardScore.getScoreAsInteger(viewer, replacer) ?: return@scoresForEach
                    val line = scoreboardScore.getLine(viewer) ?: return@scoresForEach

                    scores[score] = if (line.shouldRender()) line.currentText(viewer, replacer) else null
                }

                println("title: $title")
                println("scores: $scores")
            }
        }
    }
}