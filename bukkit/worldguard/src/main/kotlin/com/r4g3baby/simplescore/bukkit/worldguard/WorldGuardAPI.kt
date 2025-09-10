package com.r4g3baby.simplescore.bukkit.worldguard

import com.r4g3baby.simplescore.core.util.Reflection.classExists
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

object WorldGuardAPI {
    private lateinit var worldguard: IWorldGuard

    fun initialize() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return

        if (classExists("com.sk89q.worldguard.WorldGuard")) {
            worldguard = com.r4g3baby.simplescore.bukkit.worldguard.v7.WorldGuardImpl()
        } else if (classExists("com.sk89q.worldguard.protection.flags.registry.FlagRegistry")) {
            worldguard = com.r4g3baby.simplescore.bukkit.worldguard.v6.WorldGuardImpl()
        }
    }

    fun getScoreboardFlag(player: Player, location: Location = player.location): List<String> {
        if (this::worldguard.isInitialized) {
            return worldguard.getScoreboardFlag(player, location)
        }
        return emptyList()
    }
}