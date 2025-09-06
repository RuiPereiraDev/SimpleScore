package com.r4g3baby.simplescore.core.scoreboard.condition

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition

data class LessThan<V : Any>(
    override val name: String,
    val input: String, val parseInput: Boolean,
    val value: String, val parseValue: Boolean,
    val orEqual: Boolean
) : Condition<V> {
    override fun check(viewer: V, varReplacer: VarReplacer<V>): Boolean {
        val parsedInput = if (parseInput) varReplacer.replace(input, viewer) else input
        val parsedValue = if (parseValue) varReplacer.replace(value, viewer) else value

        val inputAsDouble = parsedInput.toDoubleOrNull()
        val valueAsDouble = parsedValue.toDoubleOrNull()

        return if (inputAsDouble != null && valueAsDouble != null) {
            val numericCompareResult = inputAsDouble.compareTo(valueAsDouble)
            if (orEqual) numericCompareResult <= 0 else numericCompareResult < 0
        } else {
            val stringCompareResult = parsedInput.compareTo(parsedValue)
            if (orEqual) stringCompareResult <= 0 else stringCompareResult < 0
        }
    }
}