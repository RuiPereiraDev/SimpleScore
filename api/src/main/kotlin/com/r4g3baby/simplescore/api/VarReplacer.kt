package com.r4g3baby.simplescore.api

public fun interface VarReplacer<V : Any> {
    public fun replace(text: String, viewer: V): String
}