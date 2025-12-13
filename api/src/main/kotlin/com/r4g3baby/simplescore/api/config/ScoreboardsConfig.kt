package com.r4g3baby.simplescore.api.config

import com.r4g3baby.simplescore.api.scoreboard.Scoreboard

/**
 * Represents the configuration for scoreboards.
 *
 * @param V The type of the platform-specific player object.
 */
public interface ScoreboardsConfig<V : Any> {
    /**
     * A map of all defined scoreboards, with the scoreboard name as the key.
     */
    public val scoreboards: Map<String, Scoreboard<V>>
}
