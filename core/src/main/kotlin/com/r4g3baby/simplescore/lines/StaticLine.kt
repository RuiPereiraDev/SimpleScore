package com.r4g3baby.simplescore.lines

import com.r4g3baby.simplescore.api.Condition
import com.r4g3baby.simplescore.api.ScoreboardLine
import com.r4g3baby.simplescore.api.TextEffect
import com.r4g3baby.simplescore.api.VarReplacer

class StaticLine<V : Any>(
    val text: String,
    val renderEvery: Int = ScoreboardLine.DEFAULT_RENDER_TICKS,
    override val textEffects: List<TextEffect> = emptyList(),
    override val conditions: List<Condition<V>> = emptyList()
) : ScoreboardLine<V> {
    private var currentTick = 1

    override fun tick() {
        if (currentTick++ >= renderEvery) {
            currentTick = 1
        }
    }

    override fun shouldRender(): Boolean {
        return currentTick == 1
    }

    override fun currentText(viewer: V, varReplacer: VarReplacer<V>): String {
        return applEffects(varReplacer.replace(text, viewer))
    }

    override fun toString(): String {
        return "StaticLine(text=$text, renderEvery=$renderEvery, textEffects=$textEffects, conditions=$conditions)"
    }
}