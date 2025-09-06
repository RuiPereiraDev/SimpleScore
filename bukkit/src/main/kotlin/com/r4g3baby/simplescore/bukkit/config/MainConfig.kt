package com.r4g3baby.simplescore.bukkit.config

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.core.config.BaseMainConfig
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.Reader
import java.util.function.Predicate
import java.util.regex.Pattern

class MainConfig(plugin: BukkitPlugin) : BaseMainConfig<Player, YamlConfiguration>(plugin.dataFolder) {
    override val conditionsConfig = ConditionsConfig(plugin)
    override val scoreboardsConfig = ScoreboardsConfig(plugin, this)

    override val resourceName: String = "configs/main.yml"

    var taskUpdateTime: Long = 1
    var scoreboardTaskAsync: Boolean = true

    private val _worlds = LinkedHashMap<Predicate<String>, List<String>>()
    val worlds: Map<Predicate<String>, List<String>> get() = _worlds

    override fun parseConfigFile(reader: Reader?): YamlConfiguration {
        return if (reader != null) {
            YamlConfiguration.loadConfiguration(reader)
        } else YamlConfiguration()
    }

    override fun loadVariables(config: YamlConfiguration) {
        version = config.getInt("version", version)
        language = config.getString("language", language)
        checkForUpdates = config.getBoolean("checkForUpdates", checkForUpdates)
        taskUpdateTime = config.getLong("taskUpdateTime", taskUpdateTime)
        scoreboardTaskAsync = config.getBoolean("scoreboardTaskAsync", scoreboardTaskAsync)

        if (config.isConfigurationSection("worlds")) {
            val worldsSec = config.getConfigurationSection("worlds")
            worldsSec.getKeys(false).forEach { worldName ->
                val pattern = Pattern.compile(worldName, Pattern.CASE_INSENSITIVE)
                _worlds[pattern.asPredicate()] = when {
                    worldsSec.isString(worldName) -> listOf(worldsSec.getString(worldName))
                    worldsSec.isList(worldName) -> worldsSec.getStringList(worldName)
                    else -> emptyList()
                }
            }
        }
    }
}