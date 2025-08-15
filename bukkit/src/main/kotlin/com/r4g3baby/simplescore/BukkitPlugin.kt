package com.r4g3baby.simplescore

import com.r4g3baby.simplescore.api.Manager
import com.r4g3baby.simplescore.api.Platform
import com.r4g3baby.simplescore.bukkit.BukkitManager
import com.r4g3baby.simplescore.bukkit.scheduler.BukkitScheduler
import com.r4g3baby.simplescore.bukkit.scheduler.FoliaScheduler
import com.r4g3baby.simplescore.bukkit.scheduler.FoliaScheduler.Companion.isFoliaServer
import com.r4g3baby.simplescore.bukkit.scheduler.Scheduler
import com.r4g3baby.simplescore.bukkit.util.bukkitProvider
import com.r4g3baby.simplescore.bukkit.worldguard.WorldGuardAPI
import com.r4g3baby.simplescore.core.util.checkForUpdates
import org.bstats.bukkit.Metrics
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class BukkitPlugin : JavaPlugin(), Platform<Player> {
    override val provider = bukkitProvider(this)

    lateinit var scheduler: Scheduler
        private set

    override lateinit var manager: BukkitManager
        private set

    override fun onLoad() {
        scheduler = if (isFoliaServer) FoliaScheduler(this) else BukkitScheduler(this)

        manager = BukkitManager(this).apply { onLoad() }
        Manager.setInstance(manager)

        server.servicesManager.register(Manager::class.java, manager, this, ServicePriority.Normal)

        WorldGuardAPI.initialize()
    }

    override fun onEnable() {
        if (!this::scheduler.isInitialized || !this::manager.isInitialized) {
            logger.warning("Failed to initialize plugin, disabling...")
            server.pluginManager.disablePlugin(this)
            return
        }
        manager.onEnable()

        Metrics(this, ProjectInfo.BSTATS_BUKKIT_ID)

        if (manager.config.checkForUpdates) {
            scheduler.runTaskAsync {
                checkForUpdates({ newVersion ->
                    logger.warning("New version (v$newVersion) available. Download at:")
                    logger.warning(ProjectInfo.DOWNLOAD_URL)
                })
            }
        }
    }

    override fun onDisable() {
        if (this::scheduler.isInitialized) {
            scheduler.cancelTasks()
        }
        if (this::manager.isInitialized) {
            manager.onDisable()
        }
    }
}