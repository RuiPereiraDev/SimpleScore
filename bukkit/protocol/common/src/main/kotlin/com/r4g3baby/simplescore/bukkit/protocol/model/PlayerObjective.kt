package com.r4g3baby.simplescore.bukkit.protocol.model

data class PlayerObjective(
    val title: ObjectiveTitle,
    val scores: Map<String, ObjectiveScore>
)