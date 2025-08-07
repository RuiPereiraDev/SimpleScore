package com.r4g3baby.simplescore.core.scoreboard.condition

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition

abstract class Condition<V : Any> : Condition<V> {
    protected abstract val negate: Boolean
    protected abstract fun pass(viewer: V, varReplacer: VarReplacer<V>): Boolean

    override fun check(viewer: V, varReplacer: VarReplacer<V>): Boolean {
        return if (negate) !pass(viewer, varReplacer) else pass(viewer, varReplacer)
    }
}