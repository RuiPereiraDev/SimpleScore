package com.r4g3baby.simplescore.bukkit.config

import com.r4g3baby.simplescore.BukkitPlugin
import com.r4g3baby.simplescore.api.scoreboard.ScoreboardLine
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import com.r4g3baby.simplescore.api.scoreboard.effect.TextEffect
import com.r4g3baby.simplescore.core.config.BaseScoreboardsConfig
import com.r4g3baby.simplescore.core.scoreboard.Scoreboard
import com.r4g3baby.simplescore.core.scoreboard.ScoreboardScore
import com.r4g3baby.simplescore.core.scoreboard.condition.Negate
import com.r4g3baby.simplescore.core.scoreboard.line.AnimatedLine
import com.r4g3baby.simplescore.core.scoreboard.line.BlankLine
import com.r4g3baby.simplescore.core.scoreboard.line.ScoreboardLine.Companion.DEFAULT_RENDER_TICKS
import com.r4g3baby.simplescore.core.scoreboard.line.ScoreboardLine.Companion.DEFAULT_VISIBLE_TICKS
import com.r4g3baby.simplescore.core.scoreboard.line.StaticLine
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.Reader

class ScoreboardsConfig(
    private val plugin: BukkitPlugin, private val mainConfig: MainConfig
) : BaseScoreboardsConfig<Player, YamlConfiguration>(plugin.dataFolder) {
    override val resourceName: String = "configs/scoreboards.yml"

    override fun parseConfigFile(reader: Reader?): YamlConfiguration {
        return if (reader != null) {
            YamlConfiguration.loadConfiguration(reader)
        } else YamlConfiguration()
    }

    override fun loadVariables(config: YamlConfiguration) {
        config.getKeys(false).forEach { name ->
            val section = config.getConfigurationSection(name) ?: return@forEach

            val titles = section.parseScoreboardLines("titles")
            val scores = section.parseScoreboardScores()
            val hideNumbers = section.getBoolean("hideNumbers", false)
            val conditions = section.parseConditions()

            scoreboards[name] = Scoreboard(name, titles, scores, hideNumbers, conditions)
        }
    }

    private fun ConfigurationSection.parseScoreboardScores(): List<ScoreboardScore<Player>> {
        return when {
            isList("scores") -> {
                val scoresMapList = getMapList("scores")
                mutableListOf<ScoreboardScore<Player>>().also { scores ->
                    scoresMapList.forEachIndexed forEachScore@{ i, scoreMap ->
                        val section = MemoryConfiguration().createSection("${this.currentPath}.scores[$i]").apply {
                            scoreMap.forEach { (key, value) -> addDefault(key.toString(), value) }
                        }

                        val score = section.get("score")?.toString() ?: run {
                            plugin.logger.warning("Missing 'score' value for '${section.currentPath}'.")
                            return@forEachScore
                        }
                        val lines = section.parseScoreboardLines("lines")
                        val hideNumber = section.getBoolean("hideNumber", false)
                        val conditions = section.parseConditions()

                        scores.add(ScoreboardScore(score, lines, hideNumber, conditions))
                    }
                }
            }

            isConfigurationSection("scores") -> {
                val section = getConfigurationSection("scores")
                mutableListOf<ScoreboardScore<Player>>().also { scores ->
                    section.getKeys(false).forEach forEachScore@{ score ->
                        val scoreSec = section.getConfigurationSection(score) ?: run {
                            scores.add(ScoreboardScore(score, section.parseScoreboardLines(score)))
                            return@forEachScore
                        }

                        val lines = scoreSec.parseScoreboardLines("lines")
                        val hideNumber = scoreSec.getBoolean("hideNumber", false)
                        val conditions = scoreSec.parseConditions()
                        scores.add(ScoreboardScore(score, lines, hideNumber, conditions))

                    }
                }
            }

            else -> {
                plugin.logger.warning("Invalid or missing 'scores' value for '${this.currentPath}'.")
                return emptyList()
            }
        }
    }

    private fun ConfigurationSection.parseScoreboardLines(path: String): List<ScoreboardLine<Player>> {
        return when {
            isString(path) -> {
                val text = getString(path)
                listOf(if (text.isBlank()) BlankLine() else StaticLine(text))
            }

            isList(path) -> {
                if (getList(path).any { it !is String }) {
                    mutableListOf<ScoreboardLine<Player>>().also { lineList ->
                        getList(path).forEachIndexed { i, line ->
                            if (line !is Map<*, *>) {
                                if (line is String) {
                                    lineList.add(StaticLine(line))
                                    return@forEachIndexed
                                }
                                plugin.logger.warning("Invalid frame value for '${this.currentPath}.$path[$i]'.")
                                return@forEachIndexed
                            }

                            val section = MemoryConfiguration().createSection("${this.currentPath}.$path[$i]").apply {
                                line.forEach { (key, value) -> addDefault(key.toString(), value) }
                            }
                            if (section.contains("frames")) {
                                lineList.add(section.parseAnimatedLine())
                            } else lineList.add(section.parseStaticLine())
                        }
                    }
                } else listOf(AnimatedLine(getStringList(path).map {
                    AnimatedLine.Frame(it)
                }))
            }

            isConfigurationSection(path) -> {
                if (contains("frames")) {
                    listOf(parseAnimatedLine())
                } else listOf(parseStaticLine())
            }

            else -> {
                plugin.logger.warning("Invalid or missing '$path' value for '${this.currentPath}'.")
                return emptyList()
            }
        }
    }

    private fun ConfigurationSection.parseStaticLine(): ScoreboardLine<Player> {
        val text = getString("text") ?: run {
            plugin.logger.warning("Missing 'text' value for '${this.currentPath}'.")
            return BlankLine()
        }

        var renderEvery = get("renderEvery")
        if (renderEvery !is Int) renderEvery = DEFAULT_RENDER_TICKS

        val textEffects = emptyList<TextEffect>()
        val conditions = parseConditions()

        return if (!text.isBlank()) {
            StaticLine(text, renderEvery, textEffects, conditions)
        } else BlankLine(conditions)
    }

    private fun ConfigurationSection.parseAnimatedLine(): ScoreboardLine<Player> {
        val textFrames = getList("frames") ?: run {
            if (isString("frames")) {
                val textEffects = emptyList<TextEffect>()
                val conditions = parseConditions()
                return StaticLine(getString("frames"), DEFAULT_RENDER_TICKS, textEffects, conditions)
            }
            plugin.logger.warning("Missing 'frames' value for '${this.currentPath}'.")
            return BlankLine()
        }

        val frames = mutableListOf<AnimatedLine.Frame>()
        textFrames.forEachIndexed { i, frame ->
            when (frame) {
                is String -> frames.add(AnimatedLine.Frame(frame))

                is Map<*, *> -> {
                    var visibleFor = frame["visibleFor"]
                    if (visibleFor !is Int) visibleFor = DEFAULT_VISIBLE_TICKS

                    var renderEvery = frame["renderEvery"]
                    if (renderEvery !is Int) renderEvery = DEFAULT_RENDER_TICKS

                    val text = frame["text"] ?: run {
                        plugin.logger.warning("Missing text value for frame '${this.currentPath}[$i]'.")
                        return@forEachIndexed
                    }

                    frames.add(AnimatedLine.Frame(text.toString(), visibleFor, renderEvery))
                }

                else -> {
                    plugin.logger.warning("Invalid frame value for '${this.currentPath}[$i]'.")
                }
            }
        }

        val textEffects = emptyList<TextEffect>()
        val conditions = parseConditions()

        return AnimatedLine(frames, textEffects, conditions)
    }

    private fun ConfigurationSection.parseConditions(): List<Condition<Player>> {
        fun getCondition(name: String): Condition<Player>? {
            return if (name.startsWith("!")) {
                val name = name.substring(1)
                mainConfig.conditions[name]?.let { Negate(it) }
            } else mainConfig.conditions[name]
        }

        return when {
            isString("conditions") -> {
                val name = getString("conditions")
                listOf(getCondition(name) ?: run {
                    plugin.logger.warning("Unknown condition '$name' in '${this.currentPath}.conditions'.")
                    return emptyList()
                })
            }

            isList("conditions") -> getStringList("conditions").mapNotNull {
                getCondition(it) ?: run {
                    plugin.logger.warning("Unknown condition '$it' in '${this.currentPath}.conditions'.")
                    return@mapNotNull null
                }
            }

            else -> emptyList()
        }
    }
}