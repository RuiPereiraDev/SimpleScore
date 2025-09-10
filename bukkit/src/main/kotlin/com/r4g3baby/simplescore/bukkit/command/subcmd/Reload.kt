package com.r4g3baby.simplescore.bukkit.command.subcmd

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.bukkit.command.SubCmd
import org.bukkit.command.CommandSender

class Reload(private val plugin: BukkitPlugin) : SubCmd(plugin, "reload") {
    override fun run(sender: CommandSender, args: Array<out String>) {
        sender.sendMessage(plugin.i18n.t("cmd.reload.start"))
        plugin.manager.loadConfiguration()
        sender.sendMessage(plugin.i18n.t("cmd.reload.finished"))
    }
}