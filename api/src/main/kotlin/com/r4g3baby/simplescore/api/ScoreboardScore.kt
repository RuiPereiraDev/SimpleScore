package com.r4g3baby.simplescore.api

public interface ScoreboardScore<V : Any> : Conditional<V> {
    public val score: String

    public val lines: List<ScoreboardLine<V>>

    public fun getScoreAsInteger(viewer: V, varReplacer: VarReplacer<V>): Int?

    public fun getLine(viewer: V): ScoreboardLine<V>?
}