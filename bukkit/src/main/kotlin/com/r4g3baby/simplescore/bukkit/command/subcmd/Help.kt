package com.r4g3baby.simplescore.bukkit.command.subcmd

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.bukkit.command.MainCmd
import com.r4g3baby.simplescore.bukkit.command.SubCmd
import org.bukkit.command.CommandSender

class Help(private val plugin: BukkitPlugin, private val mainCmd: MainCmd) : SubCmd(plugin, "help") {
    override fun run(sender: CommandSender, args: Array<out String>) {
        val commands = mainCmd.subCommands.filter { sender.hasPermission(it.permission) }
        if (commands.isNotEmpty()) {
            sender.sendMessage(plugin.i18n.t("cmd.help.header"))
            for (cmd in commands) {
                sender.sendMessage(plugin.i18n.t("cmd.help.cmd", cmd.name, cmd.description, prefixed = false))
            }
        } else sender.sendMessage(plugin.i18n.t("cmd.help.noCommands"))
    }
}