package com.r4g3baby.simplescore.api.scoreboard.effect

/**
 * Functional interface for applying visual effects to text.
 * Text effects can modify the appearance of text, such as adding colors, animations, or other visual enhancements.
 */
public fun interface TextEffect {
    /**
     * Applies the effect to the given text.
     *
     * @param text The original text to apply the effect to.
     * @return The text with the effect applied.
     */
    public fun apply(text: String): String
}
