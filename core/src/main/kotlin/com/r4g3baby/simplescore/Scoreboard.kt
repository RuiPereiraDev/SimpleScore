package com.r4g3baby.simplescore

import com.r4g3baby.simplescore.api.Condition
import com.r4g3baby.simplescore.api.Scoreboard
import com.r4g3baby.simplescore.api.ScoreboardLine
import com.r4g3baby.simplescore.api.ScoreboardScore
import java.util.*
import java.util.Collections.newSetFromMap
import java.util.Collections.synchronizedSet

// https://github.com/CatCoderr/ProtocolSidebar
class Scoreboard<V : Any>(
    override val name: String,
    override val titles: List<ScoreboardLine<V>>,
    override val scores: List<ScoreboardScore<V>>,
    override val conditions: List<Condition<V>> = emptyList()
) : Scoreboard<V> {
    private val viewers: MutableSet<V> = synchronizedSet(newSetFromMap(WeakHashMap()))

    override fun addViewer(viewer: V): Boolean {
        return viewers.add(viewer)
    }

    override fun removeViewer(viewer: V): Boolean {
        return viewers.remove(viewer)
    }

    override fun getViewers(): Set<V> {
        synchronized(viewers) {
            return viewers.toSet()
        }
    }

    override fun tick() {
        titles.forEach { title -> title.tick() }
        scores.forEach { score -> score.lines.forEach { line -> line.tick() } }
    }

    override fun getTitle(viewer: V): ScoreboardLine<V>? {
        return titles.firstOrNull { it.canSee(viewer) }
    }

    override fun getScores(viewer: V): List<ScoreboardScore<V>> {
        return scores.filter { it.canSee(viewer) }
    }

    override fun toString(): String {
        return "Scoreboard(name=$name, titles=$titles, scores=$scores, conditions=$conditions)"
    }
}