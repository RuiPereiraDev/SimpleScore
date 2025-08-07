package com.r4g3baby.simplescore.core.config

import com.r4g3baby.simplescore.api.config.ScoreboardsConfig
import com.r4g3baby.simplescore.core.scoreboard.Scoreboard
import com.r4g3baby.simplescore.core.scoreboard.ScoreboardScore
import com.r4g3baby.simplescore.core.scoreboard.effect.FillEffect
import com.r4g3baby.simplescore.core.scoreboard.line.AnimatedLine
import com.r4g3baby.simplescore.core.scoreboard.line.BlankLine
import com.r4g3baby.simplescore.core.scoreboard.line.StaticLine
import java.io.File

abstract class BaseScoreboardsConfig<V : Any, T : Any>(
    dataFolder: File
) : ScoreboardsConfig<V>, ConfigFile<T>(dataFolder, "scoreboards.yml") {
    override val scoreboards: MutableMap<String, Scoreboard<V>> = mutableMapOf(
        "default" to Scoreboard(
            "default", listOf(
                StaticLine("<gradient:#f79459:red>%player_uuid%", textEffects = arrayOf(FillEffect(50)))
            ), listOf(
                ScoreboardScore(
                    "%tick%", AnimatedLine(
                        listOf(
                            AnimatedLine.Frame(
                                "&#c33131This is my over 32 characters frame!!", visibleFor = 10
                            ),
                            AnimatedLine.Frame(
                                "&cThis is my second frame that lasts for 10 ticks.", visibleFor = 10
                            )
                        )
                    ), hideNumber = true
                ),
                ScoreboardScore(0, BlankLine()),
                ScoreboardScore(0, StaticLine("&#c33131Testing&c!")),
                ScoreboardScore(
                    0, StaticLine("<gradient:#5e4fa2:#f79459:red>This is a gradient test!!!")
                ),
                ScoreboardScore(
                    0, StaticLine(
                        "Hello <rainbow>world</rainbow>, isn't <underlined>MiniMessage</underlined> fun?"
                    )
                ),
                ScoreboardScore(0, StaticLine("%tick%")),
                ScoreboardScore(0, StaticLine("%player_hearts%")),
                ScoreboardScore(0, BlankLine())
            )
        )
    )
}