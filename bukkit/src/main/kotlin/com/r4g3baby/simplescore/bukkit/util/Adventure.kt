package com.r4g3baby.simplescore.bukkit.util

import com.r4g3baby.simplescore.bukkit.protocol.util.ServerVersion
import com.r4g3baby.simplescore.core.util.Reflection.classExists
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor.COLOR_CHAR

object Adventure {
    private val miniMessage: MiniMessage?
    private val textSerializer: LegacyComponentSerializer?

    init {
        val isAdventureSupported = classExists("net.kyori.adventure.text.minimessage.MiniMessage")
            && classExists("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer")

        if (isAdventureSupported) {
            miniMessage = MiniMessage.builder().strict(false).build()

            val legacySerializerBuilder = LegacyComponentSerializer.builder().character(COLOR_CHAR)
            val hexColorsSupported = ServerVersion.atOrAbove(ServerVersion.netherUpdate)
            textSerializer = if (hexColorsSupported) {
                legacySerializerBuilder.useUnusualXRepeatedCharacterHexFormat().hexColors().build()
            } else legacySerializerBuilder.build()
        } else {
            miniMessage = null
            textSerializer = null
        }
    }

    fun parseToString(text: String): String {
        return if (textSerializer != null && miniMessage != null) {
            textSerializer.serialize(
                miniMessage.deserialize(vanillaToMini(text))
            )
        } else text
    }

    private val namedToMiniTag = mapOf(
        '0' to "black", '1' to "dark_blue", '2' to "dark_green", '3' to "dark_aqua", '4' to "dark_red",
        '5' to "dark_purple", '6' to "gold", '7' to "gray", '8' to "dark_gray", '9' to "blue",
        'a' to "green", 'b' to "aqua", 'c' to "red", 'd' to "light_purple", 'e' to "yellow", 'f' to "white",
        'k' to "obf", 'l' to "b", 'm' to "st", 'n' to "u", 'o' to "i", 'r' to "reset"
    )

    private fun vanillaToMini(text: String): String {
        if (text.length <= 1) return text
        val result = StringBuilder()

        var i = 0
        while (i < text.length) {
            val char = text[i]

            // Handle the color character (COLOR_CHAR aka §)
            if (char == COLOR_CHAR && i + 1 < text.length) {
                val nextChar = text[i + 1].lowercaseChar()

                // Hex color detection (§x§F§F§F§F§F§F → <#FFFFFF>)
                if (nextChar == 'x' && i + 14 <= text.length) {
                    val hexCode = StringBuilder()
                    var validHex = true

                    for (j in 3..13 step 2) { // Read the 6 hex digits
                        val char = text[i + j].lowercaseChar()
                        if (char in '0'..'9' || char in 'a'..'f') {
                            hexCode.append(text[i + j])
                        } else {
                            validHex = false
                            break
                        }
                    }

                    if (validHex) {
                        result.append("<#${hexCode}>")

                        i += 14 // Skip over the processed hex code
                        continue
                    }
                }

                // Named color code detection (§a → <green>)
                val tagName = namedToMiniTag[nextChar]
                if (tagName != null) {
                    result.append("<$tagName>")

                    i += 2 // Skip over the processed named code
                    continue
                }
            }

            // Append the current character if no formatting was detected
            result.append(char)
            i++ // And skip to the next character
        }

        return result.toString()
    }
}