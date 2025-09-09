package com.r4g3baby.simplescore.api.scoreboard

import com.r4g3baby.simplescore.api.scoreboard.condition.Conditional

/**
 * Represents a scoreboard that can be displayed to viewers.
 * A scoreboard contains titles, scores, and conditions that determine its visibility.
 *
 * @param V The type of the platform-specific player object.
 */
public interface Scoreboard<V : Any> : Conditional<V> {
    /**
     * The unique name of the scoreboard, used for identification.
     */
    public val name: String

    /**
     * A list of lines that can be used as titles for the scoreboard.
     * The actual title displayed may vary based on conditions and animation.
     */
    public val titles: List<ScoreboardLine<V>>

    /**
     * A list of scores to be displayed on the scoreboard.
     * Each score represents a line in the scoreboard with an associated value.
     */
    public val scores: List<ScoreboardScore<V>>

    /**
     * Updates the state of the scoreboard for the next frame.
     * This method is called periodically to animate titles and scores.
     */
    public fun tick()

    /**
     * Gets the current title line to be displayed for a specific viewer.
     *
     * @param viewer The platform-specific player object for whom the title is being retrieved.
     * @param varReplacer A function to replace variables in the title text.
     * @return The current title line, or null if no title should be displayed.
     */
    public fun getTitle(viewer: V, varReplacer: VarReplacer<V>): ScoreboardLine<V>?

    /**
     * Gets the list of scores to be displayed for a specific viewer.
     *
     * @param viewer The platform-specific player object for whom the scores are being retrieved.
     * @param varReplacer A function to replace variables in the score texts.
     * @return A list of scores to be displayed to the viewer.
     */
    public fun getScores(viewer: V, varReplacer: VarReplacer<V>): List<ScoreboardScore<V>>
}
