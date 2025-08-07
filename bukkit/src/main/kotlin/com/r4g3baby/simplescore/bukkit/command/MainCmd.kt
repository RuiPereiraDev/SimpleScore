package com.r4g3baby.simplescore.bukkit.command

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.bukkit.command.subcmd.Force
import com.r4g3baby.simplescore.bukkit.command.subcmd.Help
import com.r4g3baby.simplescore.bukkit.command.subcmd.Info
import com.r4g3baby.simplescore.bukkit.command.subcmd.Reload
import com.r4g3baby.simplescore.bukkit.command.subcmd.Toggle
import com.r4g3baby.simplescore.bukkit.command.subcmd.Version
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class MainCmd(private val plugin: BukkitPlugin) : CommandExecutor, TabExecutor {
    private val helpCmd = Help(plugin, this)
    internal val subCommands = listOf(
        helpCmd, Reload(plugin), Version(plugin), Info(plugin), Toggle(plugin), Force(plugin)
    )

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            for (subCmd in subCommands) {
                if (subCmd.name.equals(args[0], true)) {
                    if (sender.hasPermission(subCmd.permission)) {
                        subCmd.run(sender, args.sliceArray(1..args.lastIndex))
                    } else sender.sendMessage(plugin.i18n.t("cmd.noPermission"))
                    return true
                }
            }

            sender.sendMessage(plugin.i18n.t("cmd.notFound"))
        } else helpCmd.run(sender, emptyArray())

        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): List<String> {
        return when {
            args.size == 1 -> subCommands.filter {
                it.name.startsWith(args[0], true) && sender.hasPermission(it.permission)
            }.map { it.name }

            args.size > 1 -> subCommands.find {
                it.name.equals(args[0], true) && sender.hasPermission(it.permission)
            }?.onTabComplete(sender, args.sliceArray(1..args.lastIndex)) ?: emptyList()

            else -> emptyList()
        }
    }
}