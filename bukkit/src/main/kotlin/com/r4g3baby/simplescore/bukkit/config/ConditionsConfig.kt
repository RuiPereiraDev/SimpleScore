package com.r4g3baby.simplescore.bukkit.config

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.bukkit.scoreboard.condition.HasPermission
import com.r4g3baby.simplescore.core.config.BaseConditionsConfig
import com.r4g3baby.simplescore.core.scoreboard.condition.*
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.Reader

class ConditionsConfig(
    private val plugin: BukkitPlugin
) : BaseConditionsConfig<Player, YamlConfiguration>(plugin.dataFolder) {
    override val resourceName: String = "configs/conditions.yml"

    override fun parseConfigFile(reader: Reader?): YamlConfiguration {
        return if (reader != null) {
            YamlConfiguration.loadConfiguration(reader)
        } else YamlConfiguration()
    }

    override fun loadVariables(config: YamlConfiguration) {
        config.getKeys(false).forEach { name ->
            val section = config.getConfigurationSection(name)
            if (section != null) {
                val type = section.getString("type") ?: run {
                    plugin.logger.warning("Missing type for condition: $name.")
                    return@forEach
                }

                if (type.equals("HasPermission", true)) {
                    conditions[name] = HasPermission(
                        name, false,
                        section.getString("permission"),
                        section.getBoolean("parsePermission", false)
                    )
                } else if (type.equals("GreaterThan", true)) {
                    conditions[name] = GreaterThan(
                        name, false,
                        section.getString("input"),
                        section.getBoolean("parseInput", true),
                        section.getString("value"),
                        section.getBoolean("parseValue", false),
                        section.getBoolean("orEqual", false)
                    )
                } else if (type.equals("LessThan", true)) {
                    conditions[name] = LessThan(
                        name, false,
                        section.getString("input"),
                        section.getBoolean("parseInput", true),
                        section.getString("value"),
                        section.getBoolean("parseValue", false),
                        section.getBoolean("orEqual", false)
                    )
                } else if (type.equals("Equals", true)) {
                    conditions[name] = Equals(
                        name, false,
                        section.getString("input"),
                        section.getBoolean("parseInput", true),
                        section.getString("value"),
                        section.getBoolean("parseValue", false),
                        section.getBoolean("ignoreCase", false)
                    )
                } else if (type.equals("Contains", true)) {
                    conditions[name] = Contains(
                        name, false,
                        section.getString("input"),
                        section.getBoolean("parseInput", true),
                        section.getString("value"),
                        section.getBoolean("parseValue", false),
                        section.getBoolean("ignoreCase", false)
                    )
                } else if (type.equals("StartsWith", true)) {
                    conditions[name] = StartsWith(
                        name, false,
                        section.getString("input"),
                        section.getBoolean("parseInput", true),
                        section.getString("value"),
                        section.getBoolean("parseValue", false),
                        section.getBoolean("ignoreCase", false)
                    )
                } else if (type.equals("EndsWith", true)) {
                    conditions[name] = EndsWith(
                        name, false,
                        section.getString("input"),
                        section.getBoolean("parseInput", true),
                        section.getString("value"),
                        section.getBoolean("parseValue", false),
                        section.getBoolean("ignoreCase", false)
                    )
                } else plugin.logger.warning("Invalid type value for conditon: $name, type: $type.")
            }
        }
    }
}