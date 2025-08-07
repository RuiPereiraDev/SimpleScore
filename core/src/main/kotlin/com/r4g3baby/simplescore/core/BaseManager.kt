package com.r4g3baby.simplescore.core

import com.r4g3baby.simplescore.api.Manager
import com.r4g3baby.simplescore.api.Platform
import com.r4g3baby.simplescore.core.config.BaseMainConfig
import com.r4g3baby.simplescore.core.i18n.I18n

abstract class BaseManager<V : Any, T : Any>(
    final override val platform: Platform<V>
) : Manager<V> {
    abstract override val config: BaseMainConfig<V, T>
    override val i18n = I18n(platform)

    open fun onLoad() {
        loadConfiguration()
    }

    open fun onEnable() {}

    open fun onDisable() {}

    override fun loadConfiguration() {
        config.loadConfig()
        i18n.loadTranslations(config.language)
    }
}