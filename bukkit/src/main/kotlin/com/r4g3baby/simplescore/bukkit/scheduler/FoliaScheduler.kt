package com.r4g3baby.simplescore.bukkit.scheduler

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.core.util.Reflection
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class FoliaScheduler(private val plugin: BukkitPlugin) : Scheduler {
    companion object {
        val isFoliaServer = Reflection.classExists("io.papermc.paper.threadedregions.RegionizedServer")
    }

    private val foliaGlobalScheduler: Any
    private val foliaAsyncScheduler: Any

    private val cancelTasks: Reflection.MethodInvoker
    private val cancelAsyncTasks: Reflection.MethodInvoker
    private val runTask: Reflection.MethodInvoker
    private val runTaskAsync: Reflection.MethodInvoker
    private val runTaskTimer: Reflection.MethodInvoker
    private val runTaskTimerAsync: Reflection.MethodInvoker

    init {
        val getGlobalRegionScheduler = Reflection.getMethodByName(Bukkit.getServer().javaClass, "getGlobalRegionScheduler")
        foliaGlobalScheduler = getGlobalRegionScheduler.invoke(Bukkit.getServer())!!

        val getAsyncScheduler = Reflection.getMethodByName(Bukkit.getServer().javaClass, "getAsyncScheduler")
        foliaAsyncScheduler = getAsyncScheduler.invoke(Bukkit.getServer())!!

        cancelTasks = Reflection.getMethodByName(foliaGlobalScheduler.javaClass, "cancelTasks")
        cancelAsyncTasks = Reflection.getMethodByName(foliaAsyncScheduler.javaClass, "cancelTasks")
        runTask = Reflection.getMethodByName(foliaGlobalScheduler.javaClass, "execute")
        runTaskAsync = Reflection.getMethodByName(foliaAsyncScheduler.javaClass, "runNow")
        runTaskTimer = Reflection.getMethodByName(foliaGlobalScheduler.javaClass, "runAtFixedRate")
        runTaskTimerAsync = Reflection.getMethodByName(foliaAsyncScheduler.javaClass, "runAtFixedRate")
    }

    override fun cancelTasks() {
        cancelTasks.invoke(foliaGlobalScheduler, plugin)
        cancelAsyncTasks.invoke(foliaAsyncScheduler, plugin)
    }

    override fun runTask(task: Runnable) {
        runTask.invoke(foliaGlobalScheduler, plugin, task)
    }

    override fun runTaskAsync(task: Runnable) {
        val task: Consumer<Any> = Consumer { task.run() }
        runTaskAsync.invoke(foliaAsyncScheduler, plugin, task)
    }

    override fun runTaskTimer(delay: Long, period: Long, task: Runnable) {
        val task: Consumer<Any> = Consumer { task.run() }
        runTaskTimer.invoke(foliaGlobalScheduler, plugin, task, delay, period)
    }

    override fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable) {
        val task: Consumer<Any> = Consumer { task.run() }
        runTaskTimerAsync.invoke(foliaAsyncScheduler, plugin, task, delay * 50, period * 50, TimeUnit.MILLISECONDS)
    }
}