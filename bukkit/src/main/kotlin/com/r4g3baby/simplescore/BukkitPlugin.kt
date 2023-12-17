package com.r4g3baby.simplescore

import com.r4g3baby.simplescore.bukkit.ScoreboardTask
import com.r4g3baby.simplescore.bukkit.protocol.Testing
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class BukkitPlugin : JavaPlugin(), Listener {
    private lateinit var task: ScoreboardTask

    override fun onEnable() {
        task = ScoreboardTask(this).apply {
            runTaskTimerAsynchronously(this@BukkitPlugin, 20L, 1L)
        }

        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        task.scoreboards.forEach { it.addViewer(e.player) }

        server.scheduler.runTaskLater(this, Runnable {
            Testing(e.player)
        }, 30L)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        task.scoreboards.forEach { it.removeViewer(e.player) }
    }
}