package com.r4g3baby.simplescore.bukkit.scoreboard

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.bukkit.util.Adventure
import com.r4g3baby.simplescore.bukkit.util.getPlayerPing
import com.r4g3baby.simplescore.bukkit.util.lazyReplace
import com.r4g3baby.simplescore.core.util.translateColorCodes
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Statistic
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class VarReplacer(plugin: BukkitPlugin) : VarReplacer<Player> {
    private val usePlaceholderAPI = plugin.server.pluginManager.getPlugin("PlaceholderAPI") != null

    override fun replace(text: String, viewer: Player): String {
        var result = if (usePlaceholderAPI) PlaceholderAPI.setPlaceholders(viewer, text) else text

        result = result
            .lazyReplace("%player_name%") { viewer.name }
            .lazyReplace("%player_displayname%") { viewer.displayName }
            .lazyReplace("%player_uuid%") { viewer.uniqueId.toString() }
            .lazyReplace("%player_level%") { viewer.level.toString() }
            .lazyReplace("%player_gamemode%") { getGameMode(viewer) }
            .lazyReplace("%player_health%") { viewer.health.roundToInt().toString() }
            .lazyReplace("%player_maxhealth%") { viewer.maxHealth.roundToInt().toString() }
            .lazyReplace("%player_hearts%") { getHearts(viewer) }
            .lazyReplace("%player_ping%") { getPing(viewer) }
            .lazyReplace("%player_kills%") { viewer.getStatistic(Statistic.PLAYER_KILLS).toString() }
            .lazyReplace("%player_deaths%") { viewer.getStatistic(Statistic.DEATHS).toString() }
            .lazyReplace("%player_world%") { viewer.world.name }
            .lazyReplace("%player_world_online%") { viewer.world.players.size.toString() }
            .lazyReplace("%server_online%") { viewer.server.onlinePlayers.size.toString() }
            .lazyReplace("%server_maxplayers%") { viewer.server.maxPlayers.toString() }

        result = Adventure.parseToString(result)
        return translateColorCodes(result)
    }

    private fun getGameMode(player: Player): String {
        return player.gameMode.name.lowercase().replaceFirstChar { it.titlecase() }
    }

    private val fullHearts = Array(11) { "❤".repeat(it) }
    private fun getHearts(player: Player): String {
        val hearts = min(10, max(0, ((player.health / player.maxHealth) * 10).roundToInt()))
        return "&c${fullHearts[hearts]}&0${fullHearts[10 - hearts]}"
    }

    private fun getPing(player: Player): String {
        return getPlayerPing.apply(player).toString()
    }
}
