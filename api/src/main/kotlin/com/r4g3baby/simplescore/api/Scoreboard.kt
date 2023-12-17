package com.r4g3baby.simplescore.api

public interface Scoreboard<V : Any> : Conditional<V> {
    public val name: String

    public val titles: List<ScoreboardLine<V>>

    public val scores: List<ScoreboardScore<V>>

    public fun addViewer(viewer: V): Boolean
    public fun removeViewer(viewer: V): Boolean
    public fun getViewers(): Set<V>

    public fun tick()

    public fun getTitle(viewer: V): ScoreboardLine<V>?
    public fun getScores(viewer: V): List<ScoreboardScore<V>>
}