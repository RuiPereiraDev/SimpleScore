package com.r4g3baby.simplescore.bukkit.protocol.model

data class ObjectiveScore(
    val lineUID: String,
    val text: String,
    val value: Int,
    val hideNumber: Boolean
)