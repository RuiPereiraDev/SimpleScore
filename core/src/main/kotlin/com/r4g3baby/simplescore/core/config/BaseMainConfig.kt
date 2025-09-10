package com.r4g3baby.simplescore.core.config

import com.r4g3baby.simplescore.api.config.MainConfig
import java.io.File

abstract class BaseMainConfig<V : Any, T : Any>(
    dataFolder: File
) : MainConfig<V>, ConfigFile<T>(dataFolder, "config.yml") {
    override var version: Int = 0
    override var language: String = "en"
    override var checkForUpdates: Boolean = true

    abstract override val conditionsConfig: BaseConditionsConfig<V, T>
    abstract override val scoreboardsConfig: BaseScoreboardsConfig<V, T>

    override fun loadConfig() {
        super.loadConfig()
        conditionsConfig.loadConfig()
        scoreboardsConfig.loadConfig()
    }
}