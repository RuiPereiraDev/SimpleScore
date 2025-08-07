package com.r4g3baby.simplescore.bukkit.scheduler

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.core.util.Reflection
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class FoliaScheduler(private val plugin: BukkitPlugin): Scheduler {
    companion object {
        val isFoliaServer = Reflection.classExists("io.papermc.paper.threadedregions.RegionizedServer")
    }

    private val foliaAsyncScheduler: Any
    private val cancelTasks: Reflection.MethodInvoker
    private val runTaskAsync: Reflection.MethodInvoker
    private val runTaskTimerAsync: Reflection.MethodInvoker

    init {
        val getAsyncScheduler = Reflection.getMethodByName(Bukkit.getServer().javaClass, "getAsyncScheduler")
        foliaAsyncScheduler = getAsyncScheduler.invoke(Bukkit.getServer())!!

        cancelTasks = Reflection.getMethodByName(foliaAsyncScheduler.javaClass, "cancelTasks")
        runTaskAsync = Reflection.getMethodByName(foliaAsyncScheduler.javaClass, "runNow")
        runTaskTimerAsync = Reflection.getMethodByName(foliaAsyncScheduler.javaClass, "runAtFixedRate")
    }

    override fun cancelTasks() {
        cancelTasks.invoke(foliaAsyncScheduler, plugin)
    }

    override fun runTaskAsync(task: Runnable) {
        val task: Consumer<Any> = Consumer { task.run() }
        runTaskAsync.invoke(foliaAsyncScheduler, plugin, task)
    }

    override fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable) {
        val task: Consumer<Any> = Consumer { task.run() }
        runTaskTimerAsync.invoke(foliaAsyncScheduler, plugin, task, delay * 50, period * 50, TimeUnit.MILLISECONDS)
    }
}