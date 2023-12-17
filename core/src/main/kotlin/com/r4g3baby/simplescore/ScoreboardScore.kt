package com.r4g3baby.simplescore

import com.r4g3baby.simplescore.api.Condition
import com.r4g3baby.simplescore.api.ScoreboardLine
import com.r4g3baby.simplescore.api.ScoreboardScore
import com.r4g3baby.simplescore.api.VarReplacer

class ScoreboardScore<V : Any>(
    override val score: String,
    override val lines: List<ScoreboardLine<V>>,
    override val conditions: List<Condition<V>> = emptyList()
) : ScoreboardScore<V> {
    constructor(
        score: Int, lines: List<ScoreboardLine<V>>, conditions: List<Condition<V>> = emptyList()
    ) : this(score.toString(), lines, conditions)

    constructor(
        score: String, line: ScoreboardLine<V>, conditions: List<Condition<V>> = emptyList()
    ) : this(score, listOf(line), conditions)

    constructor(
        score: Int, line: ScoreboardLine<V>, conditions: List<Condition<V>> = emptyList()
    ) : this(score.toString(), listOf(line), conditions)

    private val scoreAsInt = score.toIntOrNull()
    override fun getScoreAsInteger(viewer: V, varReplacer: VarReplacer<V>): Int? {
        return scoreAsInt ?: varReplacer.replace(score, viewer).toIntOrNull()
    }

    override fun getLine(viewer: V): ScoreboardLine<V>? {
        return lines.firstOrNull { it.canSee(viewer) }
    }

    override fun toString(): String {
        return "ScoreboardScore(score=$score, lines=$lines, conditions=$conditions)"
    }
}