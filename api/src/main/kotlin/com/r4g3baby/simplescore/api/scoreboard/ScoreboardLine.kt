package com.r4g3baby.simplescore.api.scoreboard

import com.r4g3baby.simplescore.api.scoreboard.condition.Conditional
import com.r4g3baby.simplescore.api.scoreboard.effect.TextEffect

/**
 * Represents a line of text that can be displayed on a scoreboard.
 * A scoreboard line can have text effects applied to it and conditions that determine its visibility.
 *
 * @param V The type of the platform-specific player object.
 */
public interface ScoreboardLine<V : Any> : Conditional<V> {
    /**
     * A list of text effects to be applied to the line's text.
     * These effects can modify the appearance of the text, such as adding colors or animations.
     */
    public val textEffects: List<TextEffect>

    /**
     * A unique identifier for this line.
     * This is used to distinguish between different lines in the scoreboard.
     */
    public val uid: String

    /**
     * Updates the state of the line for the next frame.
     * This method is called periodically to animate the line.
     */
    public fun tick()

    /**
     * Determines whether the line should be rendered in the current frame.
     *
     * @return True if the line should be rendered, false otherwise.
     */
    public fun shouldRender(): Boolean

    /**
     * Gets the current text to be displayed for a specific viewer.
     *
     * @param viewer The platform-specific player object for whom the text is being retrieved.
     * @param varReplacer A function to replace variables in the text.
     * @return The current text to be displayed.
     */
    public fun currentText(viewer: V, varReplacer: VarReplacer<V>): String

    /**
     * Applies all text effects to the given text.
     *
     * @param text The original text to apply effects to.
     * @return The text with all effects applied.
     */
    public fun ScoreboardLine<V>.applyEffects(text: String): String {
        var finalText = text
        textEffects.forEach { textEffect ->
            finalText = textEffect.apply(finalText)
        }
        return finalText
    }
}
