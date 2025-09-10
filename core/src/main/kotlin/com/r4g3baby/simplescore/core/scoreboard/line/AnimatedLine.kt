package com.r4g3baby.simplescore.core.scoreboard.line

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import com.r4g3baby.simplescore.api.scoreboard.effect.TextEffect

class AnimatedLine<V : Any>(
    val frames: List<Frame>,
    override val textEffects: List<TextEffect> = emptyList(),
    override val conditions: List<Condition<V>> = emptyList()
) : ScoreboardLine<V>() {
    class Frame(
        val text: String,
        val visibleFor: Int = DEFAULT_VISIBLE_TICKS,
        val renderEvery: Int = DEFAULT_RENDER_TICKS
    ) {
        override fun toString(): String {
            return "Frame(text=$text, visibleFor=$visibleFor, renderEvery=$renderEvery)"
        }
    }

    private var currentIndex = 0
    private var currentTick = 1

    override fun tick() {
        if (frames.isEmpty()) return

        if (currentTick++ >= frames[currentIndex].visibleFor) {
            if (currentIndex++ >= (frames.size - 1)) {
                currentIndex = 0
            }
            currentTick = 1
        }
    }

    override fun shouldRender(): Boolean {
        if (frames.isEmpty()) return false

        // If the current tick is 1 we know the frame just changed
        if (currentTick == 1) return true
        val frame = frames[currentIndex]

        // Will render at the start of the next frame instead
        if (frame.visibleFor == currentTick && frame.renderEvery == currentTick) return false
        return (currentTick % frame.renderEvery) == 0
    }

    override fun currentText(viewer: V, varReplacer: VarReplacer<V>): String {
        if (frames.isEmpty()) return ""

        return applyEffects(varReplacer.replace(frames[currentIndex].text, viewer))
    }

    override fun toString(): String {
        return "AnimatedLine(frames=$frames, textEffects=$textEffects, conditions=$conditions)"
    }
}