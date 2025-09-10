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
            val section = config.getConfigurationSection(name) ?: return@forEach

            val type = section.getString("type") ?: run {
                plugin.logger.warning("Missing type key for condition: $name.")
                return@forEach
            }

            if (type.equals("HasPermission", true)) {
                conditions[name] = HasPermission(
                    name,
                    section.getString("permission") ?: run {
                        plugin.logger.warning("Missing permission key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parsePermission", false)
                )
            } else if (type.equals("GreaterThan", true)) {
                conditions[name] = GreaterThan(
                    name,
                    section.getString("input") ?: run {
                        plugin.logger.warning("Missing input key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseInput", true),
                    section.getString("value") ?: run {
                        plugin.logger.warning("Missing value key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseValue", false),
                    section.getBoolean("orEqual", false)
                )
            } else if (type.equals("LessThan", true)) {
                conditions[name] = LessThan(
                    name,
                    section.getString("input") ?: run {
                        plugin.logger.warning("Missing input key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseInput", true),
                    section.getString("value") ?: run {
                        plugin.logger.warning("Missing value key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseValue", false),
                    section.getBoolean("orEqual", false)
                )
            } else if (type.equals("Equals", true)) {
                conditions[name] = Equals(
                    name,
                    section.getString("input") ?: run {
                        plugin.logger.warning("Missing input key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseInput", true),
                    section.getString("value") ?: run {
                        plugin.logger.warning("Missing value key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseValue", false),
                    section.getBoolean("ignoreCase", false)
                )
            } else if (type.equals("Contains", true)) {
                conditions[name] = Contains(
                    name,
                    section.getString("input") ?: run {
                        plugin.logger.warning("Missing input key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseInput", true),
                    section.getString("value") ?: run {
                        plugin.logger.warning("Missing value key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseValue", false),
                    section.getBoolean("ignoreCase", false)
                )
            } else if (type.equals("StartsWith", true)) {
                conditions[name] = StartsWith(
                    name,
                    section.getString("input") ?: run {
                        plugin.logger.warning("Missing input key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseInput", true),
                    section.getString("value") ?: run {
                        plugin.logger.warning("Missing value key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseValue", false),
                    section.getBoolean("ignoreCase", false)
                )
            } else if (type.equals("EndsWith", true)) {
                conditions[name] = EndsWith(
                    name,
                    section.getString("input") ?: run {
                        plugin.logger.warning("Missing input key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseInput", true),
                    section.getString("value") ?: run {
                        plugin.logger.warning("Missing value key for condition: $name.")
                        return@forEach
                    },
                    section.getBoolean("parseValue", false),
                    section.getBoolean("ignoreCase", false)
                )
            } else plugin.logger.warning("Invalid type value for condition: $name, type: $type.")
        }
    }
}