package com.r4g3baby.simplescore.bukkit.scheduler

interface Scheduler {
    fun cancelTasks()
    fun runTaskAsync(task: Runnable)
    fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable)
}