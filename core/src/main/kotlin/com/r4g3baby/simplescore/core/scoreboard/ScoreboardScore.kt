package com.r4g3baby.simplescore.core.scoreboard

import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import com.r4g3baby.simplescore.api.scoreboard.ScoreboardLine
import com.r4g3baby.simplescore.api.scoreboard.ScoreboardScore
import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import java.util.concurrent.atomic.AtomicInteger

class ScoreboardScore<V : Any>(
    override val value: String,
    override val lines: List<ScoreboardLine<V>>,
    override val hideNumber: Boolean = false,
    override val conditions: List<Condition<V>> = emptyList(),
) : ScoreboardScore<V> {
    constructor(
        value: Int, lines: List<ScoreboardLine<V>>, hideNumber: Boolean = false, conditions: List<Condition<V>> = emptyList()
    ) : this(value.toString(), lines, hideNumber, conditions)

    constructor(
        value: String, line: ScoreboardLine<V>, hideNumber: Boolean = false, conditions: List<Condition<V>> = emptyList()
    ) : this(value, listOf(line), hideNumber, conditions)

    constructor(
        value: Int, line: ScoreboardLine<V>, hideNumber: Boolean = false, conditions: List<Condition<V>> = emptyList()
    ) : this(value.toString(), listOf(line), hideNumber, conditions)

    companion object {
        private val counter = AtomicInteger(0)
        fun getNextIdentifier(): String {
            // Base36 keeps the identifier short: legacy/teams protocol handlers encode each
            // character into a 16-char team name (2n + 2), which breaks with decimal ids >= 10000.
            return "sbs${counter.getAndIncrement().toString(36)}"
        }
    }

    override val uid = getNextIdentifier()

    private val valueAsInt = value.toIntOrNull()
    override fun getValueAsInteger(viewer: V, varReplacer: VarReplacer<V>): Int? {
        return valueAsInt ?: varReplacer.replace(value, viewer).toIntOrNull()
    }

    override fun getLine(viewer: V, varReplacer: VarReplacer<V>): ScoreboardLine<V>? {
        return lines.firstOrNull { it.canSee(viewer, varReplacer) }
    }

    override fun toString(): String {
        return "ScoreboardScore(value=$value, lines=$lines, hideNumber=$hideNumber, conditions=$conditions)"
    }
}
