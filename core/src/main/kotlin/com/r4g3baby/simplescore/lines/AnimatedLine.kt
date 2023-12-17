package com.r4g3baby.simplescore.lines

import com.r4g3baby.simplescore.api.Condition
import com.r4g3baby.simplescore.api.ScoreboardLine
import com.r4g3baby.simplescore.api.TextEffect
import com.r4g3baby.simplescore.api.VarReplacer

class AnimatedLine<V : Any>(
    val frames: List<Frame>,
    override val textEffects: List<TextEffect> = emptyList(),
    override val conditions: List<Condition<V>> = emptyList()
) : ScoreboardLine<V> {
    class Frame(
        val text: String,
        val visibleFor: Int = ScoreboardLine.DEFAULT_UPDATE_TICKS,
        val renderEvery: Int = ScoreboardLine.DEFAULT_RENDER_TICKS
    ) {
        override fun toString(): String {
            return "Frame(text=$text, visibleFor=$visibleFor, renderEvery=$renderEvery)"
        }
    }

    constructor(
        frame: Frame, textEffects: List<TextEffect> = emptyList(), conditions: List<Condition<V>> = emptyList()
    ) : this(listOf(frame), textEffects, conditions)

    private var currentIndex = 0
    private var currentTick = 1

    override fun tick() {
        if (currentTick++ >= frames[currentIndex].visibleFor) {
            if (currentIndex++ >= (frames.size - 1)) {
                currentIndex = 0
            }
            currentTick = 1
        }
    }

    override fun shouldRender(): Boolean {
        // If the current tick is 1 we know the frame just changed
        if (currentTick == 1) return true
        val frame = frames[currentIndex]

        // Will render at the start of the mext frame instead
        if (frame.visibleFor == currentTick && frame.renderEvery == currentTick) return false
        return (currentTick % frame.renderEvery) == 0
    }

    override fun currentText(viewer: V, varReplacer: VarReplacer<V>): String {
        return applEffects(varReplacer.replace(frames[currentIndex].text, viewer))
    }

    override fun toString(): String {
        return "AnimatedLine(frames=$frames, textEffects=$textEffects, conditions=$conditions)"
    }
}