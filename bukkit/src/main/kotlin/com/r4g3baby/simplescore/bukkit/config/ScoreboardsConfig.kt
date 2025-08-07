package com.r4g3baby.simplescore.bukkit.config

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.api.scoreboard.ScoreboardLine
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import com.r4g3baby.simplescore.core.config.BaseScoreboardsConfig
import com.r4g3baby.simplescore.core.scoreboard.Scoreboard
import com.r4g3baby.simplescore.core.scoreboard.ScoreboardScore
import com.r4g3baby.simplescore.core.scoreboard.line.AnimatedLine
import com.r4g3baby.simplescore.core.scoreboard.line.StaticLine
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.Reader

class ScoreboardsConfig(
    private val plugin: BukkitPlugin,
    private val mainConfig: MainConfig
) : BaseScoreboardsConfig<Player, YamlConfiguration>(plugin.dataFolder) {
    override val resourceName: String = "configs/scoreboards.yml"

    override fun parseConfigFile(reader: Reader?): YamlConfiguration {
        return if (reader != null) {
            YamlConfiguration.loadConfiguration(reader)
        } else YamlConfiguration()
    }

    override fun loadVariables(config: YamlConfiguration) {
        config.getKeys(false).forEach { name ->
            val section = config.getConfigurationSection(name)
            if (section != null) {
                val titlesObj = section.get("titles") ?: section.get("title") ?: run {
                    plugin.logger.warning("Missing titles value for scoreboard: $name.")
                    return@forEach
                }
                val titles = titlesObj.parseScoreboardLines() ?: return@forEach

                val scores: List<ScoreboardScore<Player>> = listOf(
                    ScoreboardScore("0", listOf(
                        StaticLine("&cTesting")
                    ))
                )
                val conditions: Array<Condition<Player>> = emptyArray()

                // todo: hideNumbers
                scoreboards[name] = Scoreboard(name, titles, scores, false, conditions)
            }
        }
    }

    private fun Any.parseScoreboardLines(): List<ScoreboardLine<Player>>? {
        return when (this) {
            is String -> listOf(StaticLine(this))
            is Collection<*> -> {
                val frames: MutableList<AnimatedLine.Frame> = mutableListOf()
                this.forEach { obj ->
                    when (obj) {
                        is String -> frames.add(AnimatedLine.Frame(obj))
                        is Map<*, *> -> null
                    }
                }
                return listOf(AnimatedLine(frames))
            }

            else -> null
        }
    }
}