package com.r4g3baby.simplescore.bukkit.scheduler

interface Scheduler {
    fun cancelTasks()
    fun runTask(task: Runnable)
    fun runTaskAsync(task: Runnable)
    fun runTaskTimer(delay: Long, period: Long, task: Runnable)
    fun runTaskTimerAsync(delay: Long, period: Long, task: Runnable)
}