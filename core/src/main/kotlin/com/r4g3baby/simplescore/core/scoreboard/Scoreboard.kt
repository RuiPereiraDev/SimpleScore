package com.r4g3baby.simplescore.core.scoreboard

import com.r4g3baby.simplescore.api.scoreboard.Scoreboard
import com.r4g3baby.simplescore.api.scoreboard.ScoreboardScore
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.ScoreboardLine

class Scoreboard<V : Any>(
    override val name: String,
    override val titles: List<ScoreboardLine<V>>,
    override val scores: List<ScoreboardScore<V>>,
    override val conditions: List<Condition<V>> = emptyList()
) : Scoreboard<V> {
    override fun tick() {
        titles.forEach { title -> title.tick() }
        scores.forEach { score -> score.lines.forEach { line -> line.tick() } }
    }

    override fun getTitle(viewer: V, varReplacer: VarReplacer<V>): ScoreboardLine<V>? {
        return titles.firstOrNull { it.canSee(viewer, varReplacer) }
    }

    override fun getScores(viewer: V, varReplacer: VarReplacer<V>): List<ScoreboardScore<V>> {
        return scores.filter { it.canSee(viewer, varReplacer) }
    }

    override fun toString(): String {
        return "Scoreboard(name=$name, titles=$titles, scores=$scores, conditions=$conditions)"
    }
}