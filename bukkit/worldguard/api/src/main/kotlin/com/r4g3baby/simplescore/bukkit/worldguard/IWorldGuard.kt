package com.r4g3baby.simplescore.bukkit.worldguard

import org.bukkit.Location
import org.bukkit.entity.Player

public interface IWorldGuard {
    public fun getScoreboardFlag(player: Player, location: Location): List<String>
}