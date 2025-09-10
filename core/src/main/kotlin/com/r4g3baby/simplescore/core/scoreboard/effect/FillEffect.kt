package com.r4g3baby.simplescore.core.scoreboard.effect

import com.r4g3baby.simplescore.api.scoreboard.effect.TextEffect
import com.r4g3baby.simplescore.core.util.stripColorCodes

class FillEffect(private val length: Int) : TextEffect {
    override fun apply(text: String): String {
        val padding = length - stripColorCodes(text).length
        if (padding <= 0) return text
        val sb = StringBuilder(text.length + padding)
        for (i in 1..(padding / 2)) sb.append(" ")
        sb.append(text)
        for (i in 1..(padding / 2)) sb.append(" ")
        return sb.toString()
    }
}