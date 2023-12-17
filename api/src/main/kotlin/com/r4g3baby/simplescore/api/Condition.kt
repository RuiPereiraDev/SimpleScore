package com.r4g3baby.simplescore.api

public interface Condition<V : Any> {
    public val name: String
    public fun check(viewer: V): Boolean
}