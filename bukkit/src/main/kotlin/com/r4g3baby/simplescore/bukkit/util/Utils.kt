package com.r4g3baby.simplescore.bukkit.util

import com.r4g3baby.simplescore.api.scoreboard.data.Provider
import org.bukkit.plugin.Plugin

fun String.lazyReplace(oldValue: String, newValueFunc: () -> String): String {
    run {
        var occurrenceIndex: Int = indexOf(oldValue, 0, true)
        if (occurrenceIndex < 0) return this

        val newValue = newValueFunc()

        val oldValueLength = oldValue.length
        val searchStep = oldValueLength.coerceAtLeast(1)
        val newLengthHint = length - oldValueLength + newValue.length
        if (newLengthHint < 0) throw OutOfMemoryError()
        val stringBuilder = StringBuilder(newLengthHint)

        var i = 0
        do {
            stringBuilder.append(this, i, occurrenceIndex).append(newValue)
            i = occurrenceIndex + oldValueLength
            if (occurrenceIndex >= length) break
            occurrenceIndex = indexOf(oldValue, occurrenceIndex + searchStep, true)
        } while (occurrenceIndex > 0)
        return stringBuilder.append(this, i, length).toString()
    }
}

fun bukkitProvider(plugin: Plugin): Provider {
    return Provider(plugin.name)
}