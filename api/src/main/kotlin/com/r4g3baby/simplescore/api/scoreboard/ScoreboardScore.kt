package com.r4g3baby.simplescore.api.scoreboard

import com.r4g3baby.simplescore.api.scoreboard.condition.Conditional

/**
 * Represents a score entry in a scoreboard.
 * A score entry consists of an identifier, a score value, and one or more lines of text.
 *
 * @param V The type of the platform-specific player object.
 */
public interface ScoreboardScore<V : Any> : Conditional<V> {
    /**
     * A unique identifier for this score entry.
     * This is used to distinguish between different score entries in the scoreboard.
     */
    public val uid: String

    /**
     * The score value as a string.
     * This can be a fixed value or a placeholder that will be replaced at runtime.
     */
    public val value: String

    /**
     * A list of lines that can be displayed for this score entry.
     * The actual line displayed may vary based on conditions and animation.
     */
    public val lines: List<ScoreboardLine<V>>

    /**
     * Determines whether the numeric value of this score should be hidden.
     * If true, only the text content will be displayed.
     */
    public val hideNumber: Boolean

    /**
     * Gets the score value as an integer for a specific viewer.
     *
     * @param viewer The platform-specific player object for whom the score is being retrieved.
     * @param varReplacer A function to replace variables in the score value.
     * @return The score value as an integer, or null if the score cannot be parsed as an integer.
     */
    public fun getValueAsInteger(viewer: V, varReplacer: VarReplacer<V>): Int?

    /**
     * Gets the current line to be displayed for a specific viewer.
     *
     * @param viewer The platform-specific player object for whom the line is being retrieved.
     * @param varReplacer A function to replace variables in the line text.
     * @return The current line, or null if no line should be displayed.
     */
    public fun getLine(viewer: V, varReplacer: VarReplacer<V>): ScoreboardLine<V>?
}
