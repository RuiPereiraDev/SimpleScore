package com.r4g3baby.simplescore.bukkit.scheduler

import com.r4g3baby.simplescore.BukkitPlugin

class BukkitScheduler(private val plugin: BukkitPlugin) : Scheduler {
    private val bukkitScheduler = plugin.server.scheduler

    override fun cancelTasks() {
        bukkitScheduler.cancelTasks(plugin)
    }

    override fun runTask(task: Runnable) {
        bukkitScheduler.runTask(plugin, task)
    }

    override fun runTaskAsync(task: Runnable) {
        bukkitScheduler.runTaskAsynchronously(plugin, task)
    }

    override fun runTaskTimer(delay: Long, period: Long, task: Runnable) {
        bukkitScheduler.runTaskTimer(plugin, task, delay, period)
    }

    override fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable) {
        bukkitScheduler.runTaskTimerAsynchronously(plugin, task, delay, period)
    }
}