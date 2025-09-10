package com.r4g3baby.simplescore.bukkit.command.subcmd

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.bukkit.command.SubCmd
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Info(private val plugin: BukkitPlugin) : SubCmd(plugin, "info") {
    private val otherPermission = "${this.permission}.other"

    override fun run(sender: CommandSender, args: Array<out String>) {
        if (sender is Player) {
            if (args.isNotEmpty() && sender.hasPermission(otherPermission)) {
                return targetOther(sender, args)
            }

            val viewer = plugin.manager.getOrCreateViewer(sender)
            sender.sendMessage(plugin.i18n.t("cmd.info.header"))
            sender.sendMessage(plugin.i18n.t("cmd.info.scoreboard", viewer.scoreboard?.name ?: "none"))
            sender.sendMessage(plugin.i18n.t("cmd.info.isHidden", viewer.isScoreboardHidden))
        } else {
            if (args.isNotEmpty()) return targetOther(sender, args)
            sender.sendMessage(plugin.i18n.t("cmd.info.usage.console"))
        }
    }

    private fun targetOther(sender: CommandSender, args: Array<out String>) {
        val target = Bukkit.getOnlinePlayers().find { it.name.equals(args[0], true) }
        if (target == null) return sender.sendMessage(plugin.i18n.t("cmd.notOnline"))

        val viewer = plugin.manager.getOrCreateViewer(target)
        sender.sendMessage(plugin.i18n.t("cmd.info.other.header", target.name))
        sender.sendMessage(plugin.i18n.t("cmd.info.scoreboard", viewer.scoreboard?.name ?: "none"))
        sender.sendMessage(plugin.i18n.t("cmd.info.isHidden", viewer.isScoreboardHidden))
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return if (args.size == 1 && sender.hasPermission(otherPermission)) {
            getTargetsFor(sender).filter { it.startsWith(args[0], true) }
        } else emptyList()
    }
}