package com.r4g3baby.simplescore.core.scoreboard.condition

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition

data class Negate<V : Any>(val condition: Condition<V>) : Condition<V> {
    override val name: String = "!${condition.name}"

    override fun check(viewer: V, varReplacer: VarReplacer<V>): Boolean {
        return !condition.check(viewer, varReplacer)
    }
}
