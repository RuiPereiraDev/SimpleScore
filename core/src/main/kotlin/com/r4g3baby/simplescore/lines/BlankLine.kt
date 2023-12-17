package com.r4g3baby.simplescore.lines

import com.r4g3baby.simplescore.api.Condition
import com.r4g3baby.simplescore.api.ScoreboardLine
import com.r4g3baby.simplescore.api.TextEffect
import com.r4g3baby.simplescore.api.VarReplacer

class BlankLine<V : Any>(
    override val conditions: List<Condition<V>> = emptyList()
) : ScoreboardLine<V> {
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