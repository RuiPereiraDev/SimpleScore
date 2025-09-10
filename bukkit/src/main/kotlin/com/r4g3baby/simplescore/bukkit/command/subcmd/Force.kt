package com.r4g3baby.simplescore.bukkit.command.subcmd

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.api.scoreboard.data.Priority
import com.r4g3baby.simplescore.bukkit.command.SubCmd
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Force(private val plugin: BukkitPlugin) : SubCmd(plugin, "force") {
    private val otherPermission = "${this.permission}.other"

    override fun run(sender: CommandSender, args: Array<out String>) {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                val viewer = plugin.manager.getOrCreateViewer(sender)
                if (args[0].equals("none", true)) {
                    viewer.removeScoreboard(plugin.provider)
                    sender.sendMessage(plugin.i18n.t("cmd.force.removed"))
                } else {
                    val scoreboard = plugin.manager.getScoreboard(args[0])
                    if (scoreboard != null) {
                        viewer.setScoreboard(scoreboard, plugin.provider, Priority.Highest)
                        sender.sendMessage(plugin.i18n.t("cmd.force.changed", scoreboard.name))
                    } else if (sender.hasPermission(otherPermission)) {
                        targetOther(sender, args)
                    } else sender.sendMessage(plugin.i18n.t("cmd.force.notFound", args[0]))
                }
            } else {
                if (sender.hasPermission(otherPermission)) {
                    sender.sendMessage(plugin.i18n.t("cmd.force.usage.admin"))
                } else sender.sendMessage(plugin.i18n.t("cmd.force.usage.player"))
            }
        } else {
            if (args.isNotEmpty()) return targetOther(sender, args)
            sender.sendMessage(plugin.i18n.t("cmd.force.usage.console"))
        }
    }

    private fun targetOther(sender: CommandSender, args: Array<out String>) {
        if (args.size > 1) {
            val target = Bukkit.getOnlinePlayers().find { it.name.equals(args[0], true) }
            if (target == null) return sender.sendMessage(plugin.i18n.t("cmd.notOnline"))

            val viewer = plugin.manager.getOrCreateViewer(target)
            if (args[1].equals("none", true)) {
                viewer.removeScoreboard(plugin.provider)
                sender.sendMessage(plugin.i18n.t("cmd.force.other.removed", target.name))
            } else {
                val scoreboard = plugin.manager.getScoreboard(args[1])
                if (scoreboard != null) {
                    viewer.setScoreboard(scoreboard, plugin.provider, Priority.Highest)
                    sender.sendMessage(plugin.i18n.t("cmd.force.other.changed", target.name, scoreboard.name))
                } else sender.sendMessage(plugin.i18n.t("cmd.force.notFound", args[1]))
            }
        } else {
            if (sender is Player) {
                sender.sendMessage(plugin.i18n.t("cmd.force.usage.admin"))
            } else sender.sendMessage(plugin.i18n.t("cmd.force.usage.console"))
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> mutableListOf("none").apply {
                if (sender is Player) {
                    addAll(plugin.manager.scoreboards.map { it.name })
                } else clear()

                if (sender.hasPermission(otherPermission)) addAll(getTargetsFor(sender))
            }.filter { it.startsWith(args[0], true) }

            2 -> mutableListOf("none").apply {
                if (sender.hasPermission(otherPermission)) {
                    addAll(plugin.manager.scoreboards.map { it.name })
                } else clear()
            }.filter { it.startsWith(args[1], true) }

            else -> emptyList()
        }
    }
}