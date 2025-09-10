package com.r4g3baby.simplescore.api.config

import com.r4g3baby.simplescore.api.scoreboard.condition.Condition

/**
 * Represents the configuration for conditions used in the plugin.
 * This interface provides access to all defined conditions.
 *
 * @param V The type of the platform-specific player object.
 */
public interface ConditionsConfig<V : Any> {
    /**
     * A map of condition names to condition objects.
     * This provides access to all conditions defined in the configuration.
     * Each condition can be referenced by its name and used to determine visibility of scoreboards and lines.
     */
    public val conditions: Map<String, Condition<V>>
}
