package com.r4g3baby.simplescore.core.util

private const val COLOR_CHAR = '\u00A7'
private const val ALT_COLOR_CHAR = '\u0026'
private const val HEX_CHARS = "0123456789AaBbCcDdEeFf"
private const val COLOR_CHARS = "0123456789abcdefklmnor"

fun translateColorCodes(text: String): String {
    if (text.length <= 1) return text
    val result = StringBuilder()

    var i = 0
    while (i < text.length) {
        val char = text[i]

        // Handle the alt color character
        if (char == ALT_COLOR_CHAR && i + 1 < text.length) {
            val nextChar = text[i + 1]

            // Hex color code detection (&#RRGGBB → §x§R§R§G§G§B§B)
            if (nextChar == '#' && i + 7 < text.length) {
                val hexCode = StringBuilder("${COLOR_CHAR}x")
                var validHex = true

                for (j in 2..7) { // Read the 6 hex digits
                    val hexChar = text[i + j]
                    if (hexChar !in HEX_CHARS) {
                        validHex = false; break
                    }

                    hexCode.append(COLOR_CHAR).append(hexChar)
                }

                if (validHex) {
                    result.append(hexCode)

                    i += 8 // Skip over the processed hex code
                    continue
                }
            }

            // Color code detection (&a → §a)
            if (nextChar in COLOR_CHARS) {
                result.append(COLOR_CHAR).append(nextChar)

                i += 2 // Skip over the processed color code
                continue
            }
        }

        // Append the current character if no formatting was detected
        result.append(char)
        i++ // And skip to the next character
    }

    return result.toString()
}

private val colorRegex = Regex("[§&]x(?:[§&][a-fA-F0-9]){6}|[§&][0-9a-fk-orA-FK-OR]")

fun stripColorCodes(text: String): String {
    return colorRegex.replace(text, "")
}