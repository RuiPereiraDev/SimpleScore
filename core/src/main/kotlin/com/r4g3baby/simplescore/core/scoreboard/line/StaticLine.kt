package com.r4g3baby.simplescore.core.scoreboard.line

import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import com.r4g3baby.simplescore.api.scoreboard.ScoreboardLine
import com.r4g3baby.simplescore.api.scoreboard.effect.TextEffect
import com.r4g3baby.simplescore.api.scoreboard.VarReplacer

class StaticLine<V : Any>(
    val text: String,
    val renderEvery: Int = ScoreboardLine.DEFAULT_RENDER_TICKS,
    override val textEffects: Array<TextEffect> = emptyArray(),
    override val conditions: Array<Condition<V>> = emptyArray()
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