package com.r4g3baby.simplescore.api

import com.r4g3baby.simplescore.api.i18n.I18n
import com.r4g3baby.simplescore.api.scoreboard.data.Provider
import java.io.File
import java.util.logging.Logger

/**
 * Represents the platform on which the plugin is running.
 * This interface provides methods to interact with the platform's resources.
 *
 * @param V The type of the platform-specific player object.
 */
public interface Platform<V : Any> {
    public val provider: Provider

    /**
     * The manager property serves as the primary interface for accessing and
     * manipulating the platform's resources related to scoreboards, viewers,
     * and configurations. It provides essential functionality needed to manage
     * the platform-specific player interactions and game elements.
     *
     * @param V The type of the platform-specific player object.
     */
    public val manager: Manager<V>

    /**
     * Provides access to the internationalization (i18n) features of the platform.
     * The i18n object enables the translation of keys into different languages,
     * optionally using arguments and prefixes, to support localization.
     *
     * @return An instance of the I18n interface that allows for the translation of strings.
     */
    public val i18n: I18n get() = manager.i18n

    /**
     * Retrieves the logger instance associated with the platform.
     *
     * @return The logger instance used for logging platform-specific messages and activities.
     */
    public fun getLogger(): Logger

    /**
     * Retrieves the data folder associated with the platform.
     *
     * @return The File object representing the data folder in the file system.
     */
    public fun getDataFolder(): File
}
