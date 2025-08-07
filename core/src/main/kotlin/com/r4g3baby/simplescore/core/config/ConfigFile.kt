package com.r4g3baby.simplescore.core.config

import java.io.File
import java.io.Reader

abstract class ConfigFile<T : Any>(parent: File, name: String) : File(parent, name) {
    protected abstract fun parseConfigFile(reader: Reader?): T
    protected abstract fun loadVariables(config: T)

    protected open val resourceName: String = this.name

    open fun loadConfig() {
        if (!this.exists()) {
            if (!this.parentFile.exists()) this.parentFile.mkdirs()

            javaClass.classLoader.getResourceAsStream(resourceName)?.use { stream ->
                this.writeBytes(stream.readBytes())
            }
        }

        val config = if (this.exists()) {
            bufferedReader().use { parseConfigFile(it) }
        } else parseConfigFile(null)

        loadVariables(config)
    }
}