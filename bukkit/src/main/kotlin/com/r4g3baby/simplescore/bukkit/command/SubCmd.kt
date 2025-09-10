package com.r4g3baby.simplescore.bukkit.command

import com.r4g3baby.simplescore.BukkitPlugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class SubCmd(plugin: BukkitPlugin, val name: String) {
    val description = plugin.i18n.t("cmd.$name.description", prefixed = false)
    val permission = "simplescore.cmd.$name"

    abstract fun run(sender: CommandSender, args: Array<out String>)

    open fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return emptyList()
    }

    protected fun getTargetsFor(sender: CommandSender): List<String> {
        var players = Bukkit.getOnlinePlayers()
        if (sender is Player) {
            players = players.filter { sender != it && sender.canSee(it) }
        }
        return players.map { it.name }
    }
}