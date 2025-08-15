package com.r4g3baby.simplescore.core.scoreboard.condition

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition

data class GreaterThan<V : Any>(
    override val name: String,
    val input: String, val parseInput: Boolean,
    val value: String, val parseValue: Boolean,
    val orEqual: Boolean
) : Condition<V> {
    override fun check(viewer: V, varReplacer: VarReplacer<V>): Boolean {
        val parsedInput = if (parseInput) varReplacer.replace(input, viewer) else input
        val parsedValue = if (parseValue) varReplacer.replace(value, viewer) else value
        val compareResult = parsedInput.compareTo(parsedValue)
        return if (orEqual) compareResult >= 0 else compareResult > 0
    }
}