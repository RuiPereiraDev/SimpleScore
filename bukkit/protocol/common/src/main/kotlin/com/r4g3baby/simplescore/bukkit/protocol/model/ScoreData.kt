package com.r4g3baby.simplescore.bukkit.protocol.model

data class ScoreData(
    val identifier: String,
    val text: String?,
    val score: Int,
    val hideNumber: Boolean
)