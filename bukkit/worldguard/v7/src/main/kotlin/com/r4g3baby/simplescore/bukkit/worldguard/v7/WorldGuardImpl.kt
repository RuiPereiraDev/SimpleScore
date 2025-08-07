package com.r4g3baby.simplescore.bukkit.worldguard.v7

import com.r4g3baby.simplescore.bukkit.worldguard.IWorldGuard
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.SetFlag
import com.sk89q.worldguard.protection.flags.StringFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import org.bukkit.Location
import org.bukkit.entity.Player

class WorldGuardImpl : IWorldGuard {
    private val worldGuard = WorldGuard.getInstance()
    private val worldGuardPlugin = WorldGuardPlugin.inst()

    private var scoreboardFlag: SetFlag<String>? = null

    init {
        val registry = worldGuard.flagRegistry
        try {
            val flag = SetFlag<String>("scoreboard", StringFlag("scoreboard"))
            registry.register(flag)
            scoreboardFlag = flag
        } catch (_: FlagConflictException) {
            val existing = registry.get("scoreboard")
            if (existing is SetFlag<*>) {
                @Suppress("UNCHECKED_CAST")
                scoreboardFlag = existing as SetFlag<String>
            }
        }
    }

    override fun getScoreboardFlag(player: Player, location: Location): List<String> {
        if (scoreboardFlag != null) {
            val world = BukkitAdapter.adapt(location.world)
            val location = BukkitAdapter.asBlockVector(location)
            val player = worldGuardPlugin.wrapPlayer(player)

            val regionManager = worldGuard.platform.regionContainer.get(world)
            val applicableRegions = regionManager?.getApplicableRegions(location)

            return applicableRegions?.queryValue(player, scoreboardFlag)?.toList() ?: emptyList()
        }
        return emptyList()
    }
}