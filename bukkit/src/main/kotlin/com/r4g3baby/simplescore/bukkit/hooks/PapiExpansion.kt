package com.r4g3baby.simplescore.bukkit.hooks

import com.r4g3baby.simplescore.BukkitPlugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class PapiExpansion(private val plugin: BukkitPlugin) : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return plugin.description.name
    }

    override fun getAuthor(): String {
        return plugin.description.authors[0]
    }

    override fun getVersion(): String {
        return plugin.description.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player == null) return null
        val viewer = plugin.manager.getViewer(player.uniqueId) ?: return null

        return when {
            params.equals("scoreboard", true) -> viewer.scoreboard?.name ?: "none"
            params.equals("isHidden", true) -> viewer.isScoreboardHidden.toString()
            else -> null
        }
    }
}
