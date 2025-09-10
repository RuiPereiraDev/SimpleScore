package com.r4g3baby.simplescore.core.config

import com.r4g3baby.simplescore.api.config.ConditionsConfig
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import java.io.File

abstract class BaseConditionsConfig<V : Any, T : Any>(
    dataFolder: File
) : ConditionsConfig<V>, ConfigFile<T>(dataFolder, "conditions.yml") {
    override val conditions: MutableMap<String, Condition<V>> = mutableMapOf()
}