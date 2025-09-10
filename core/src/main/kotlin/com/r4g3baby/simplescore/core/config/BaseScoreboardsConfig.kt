package com.r4g3baby.simplescore.core.config

import com.r4g3baby.simplescore.api.config.ScoreboardsConfig
import com.r4g3baby.simplescore.core.scoreboard.Scoreboard
import java.io.File

abstract class BaseScoreboardsConfig<V : Any, T : Any>(
    dataFolder: File
) : ScoreboardsConfig<V>, ConfigFile<T>(dataFolder, "scoreboards.yml") {
    override val scoreboards: MutableMap<String, Scoreboard<V>> = mutableMapOf()
}