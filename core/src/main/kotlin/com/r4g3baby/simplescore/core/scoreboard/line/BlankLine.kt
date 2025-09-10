package com.r4g3baby.simplescore.core.scoreboard.line

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import com.r4g3baby.simplescore.api.scoreboard.effect.TextEffect

class BlankLine<V : Any>(
    override val conditions: List<Condition<V>> = emptyList()
) : ScoreboardLine<V>() {
    override val textEffects: List<TextEffect> = emptyList()

    override fun tick() {}

    override fun shouldRender(): Boolean {
        return false
    }

    override fun currentText(viewer: V, varReplacer: VarReplacer<V>): String {
        return ""
    }

    override fun toString(): String {
        return "BlankLine(conditions=$conditions)"
    }
}