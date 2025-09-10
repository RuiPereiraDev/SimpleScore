package com.r4g3baby.simplescore.bukkit.command.subcmd

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.bukkit.command.SubCmd
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Toggle(private val plugin: BukkitPlugin) : SubCmd(plugin, "toggle") {
    private val otherPermission = "${this.permission}.other"

    override fun run(sender: CommandSender, args: Array<out String>) {
        if (sender is Player) {
            val viewer = plugin.manager.getOrCreateViewer(sender)
            if (args.isNotEmpty()) {
                if (args[0].equals("on", true)) {
                    viewer.showScoreboard(plugin.provider)
                    sender.sendMessage(plugin.i18n.t("cmd.toggle.shown"))
                } else if (args[0].equals("off", true)) {
                    viewer.hideScoreboard(plugin.provider)
                    sender.sendMessage(plugin.i18n.t("cmd.toggle.hidden"))
                } else if (sender.hasPermission(otherPermission)) {
                    targetOther(sender, args)
                } else sender.sendMessage(plugin.i18n.t("cmd.toggle.usage.player"))
            } else {
                if (!viewer.hideScoreboard(plugin.provider)) {
                    viewer.showScoreboard(plugin.provider)
                    sender.sendMessage(plugin.i18n.t("cmd.toggle.shown"))
                } else sender.sendMessage(plugin.i18n.t("cmd.toggle.hidden"))
            }
        } else {
            if (args.isNotEmpty()) return targetOther(sender, args)
            sender.sendMessage(plugin.i18n.t("cmd.toggle.usage.console"))
        }
    }

    private fun targetOther(sender: CommandSender, args: Array<out String>) {
        val target = Bukkit.getOnlinePlayers().find { it.name.equals(args[0], true) }
        if (target == null) return sender.sendMessage(plugin.i18n.t("cmd.notOnline"))

        val viewer = plugin.manager.getOrCreateViewer(target)
        if (args.size > 1) {
            if (args[1].equals("on", true)) {
                viewer.showScoreboard(plugin.provider)
                sender.sendMessage(plugin.i18n.t("cmd.toggle.other.shown", target.name))
            } else if (args[1].equals("off", true)) {
                viewer.hideScoreboard(plugin.provider)
                sender.sendMessage(plugin.i18n.t("cmd.toggle.other.hidden", target.name))
            } else {
                if (sender is Player) {
                    sender.sendMessage(plugin.i18n.t("cmd.toggle.usage.admin"))
                } else sender.sendMessage(plugin.i18n.t("cmd.toggle.usage.console"))
            }
        } else {
            if (!viewer.hideScoreboard(plugin.provider)) {
                viewer.showScoreboard(plugin.provider)
                sender.sendMessage(plugin.i18n.t("cmd.toggle.other.shown", target.name))
            } else sender.sendMessage(plugin.i18n.t("cmd.toggle.other.hidden", target.name))
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> mutableListOf("on", "off").apply {
                if (sender !is Player) clear()
                if (sender.hasPermission(otherPermission)) addAll(getTargetsFor(sender))
            }.filter { it.startsWith(args[0], true) }

            2 -> mutableListOf("on", "off").apply {
                if (args[0].equals("on", true) || args[0].equals("off", true)) clear()
                else if (!sender.hasPermission(otherPermission)) clear()
            }.filter { it.startsWith(args[1], true) }

            else -> emptyList()
        }
    }
}