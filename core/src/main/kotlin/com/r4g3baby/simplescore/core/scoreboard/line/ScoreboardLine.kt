package com.r4g3baby.simplescore.core.scoreboard.line

import com.r4g3baby.simplescore.api.scoreboard.ScoreboardLine
import java.util.concurrent.atomic.AtomicInteger

abstract class ScoreboardLine<V : Any> : ScoreboardLine<V> {
    companion object {
        const val DEFAULT_VISIBLE_TICKS: Int = 20

        const val DEFAULT_RENDER_TICKS: Int = 10

        private val counter = AtomicInteger(0)
        fun getNextIdentifier(): String {
            return "sbl${counter.getAndIncrement()}"
        }
    }

    override val uid = getNextIdentifier()
}