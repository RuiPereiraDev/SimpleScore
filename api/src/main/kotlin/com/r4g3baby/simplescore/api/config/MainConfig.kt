package com.r4g3baby.simplescore.api.config

import com.r4g3baby.simplescore.api.scoreboard.Scoreboard
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition

/**
 * Represents the main configuration for the plugin.
 * This interface provides access to various configuration settings and sub-configurations.
 *
 * @param V The type of the platform-specific player object.
 */
public interface MainConfig<V : Any> {
    /**
     * The version of the configuration.
     * This is used to track configuration changes and perform migrations if necessary.
     */
    public val version: Int

    /**
     * The language code for the plugin's messages.
     * This determines which language file will be used for internationalization.
     */
    public val language: String

    /**
     * Determines whether the plugin should check for updates.
     * If true, the plugin will periodically check for new versions.
     */
    public val checkForUpdates: Boolean

    /**
     * The configuration for conditions.
     * This provides access to condition-specific settings and definitions.
     */
    public val conditionsConfig: ConditionsConfig<V>

    /**
     * A map of condition names to condition objects.
     * This provides convenient access to all defined conditions.
     */
    public val conditions: Map<String, Condition<V>> get() = conditionsConfig.conditions

    /**
     * The configuration for scoreboards.
     * This provides access to scoreboard-specific settings and definitions.
     */
    public val scoreboardsConfig: ScoreboardsConfig<V>

    /**
     * A map of scoreboard names to scoreboard objects.
     * This provides convenient access to all defined scoreboards.
     */
    public val scoreboards: Map<String, Scoreboard<V>> get() = scoreboardsConfig.scoreboards
}
