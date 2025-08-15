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
    override val conditions: Array<Condition<V>> = emptyArray(),
) : ScoreboardScore<V> {
    constructor(
        value: Int, lines: List<ScoreboardLine<V>>, hideNumber: Boolean = false, conditions: Array<Condition<V>> = emptyArray()
    ) : this(value.toString(), lines, hideNumber, conditions)

    constructor(
        value: String, line: ScoreboardLine<V>, hideNumber: Boolean = false, conditions: Array<Condition<V>> = emptyArray()
    ) : this(value, listOf(line), hideNumber, conditions)

    constructor(
        value: Int, line: ScoreboardLine<V>, hideNumber: Boolean = false, conditions: Array<Condition<V>> = emptyArray()
    ) : this(value.toString(), listOf(line), hideNumber, conditions)

    companion object {
        private val counter = AtomicInteger(0)
        fun getNextIdentifier(): String {
            return counter.getAndIncrement().toString()
        }
    }

    override val identifier = getNextIdentifier()

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