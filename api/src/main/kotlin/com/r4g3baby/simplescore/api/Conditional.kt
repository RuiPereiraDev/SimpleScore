package com.r4g3baby.simplescore.api

public interface Conditional<V : Any> {
    public val conditions: List<Condition<V>>

    public fun canSee(viewer: V): Boolean {
        return !conditions.any { !it.check(viewer) }
    }
}