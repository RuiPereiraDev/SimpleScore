package com.r4g3baby.simplescore.api

import com.r4g3baby.simplescore.api.config.MainConfig
import com.r4g3baby.simplescore.api.i18n.I18n
import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.Scoreboard
import com.r4g3baby.simplescore.api.scoreboard.data.Viewer
import java.util.*

/**
 * Interface representing the Manager, responsible for handling platform-specific
 * resources, configurations, internationalization, variable replacement, scoreboards, and viewers.
 *
 * @param V The type of the platform-specific player object.
 */
public interface Manager<V : Any> {
    /**
     * Companion object for the Manager class that provides static access to set
     * and retrieve the singleton instance of the Manager.
     */
    public companion object {
        private var instance: Manager<*>? = null

        /**
         * Sets the instance of the Manager. This method ensures that the Manager
         * instance can only be set once, subsequent calls will result in an exception.
         *
         * @param instance The Manager instance to be set. This instance provides
         * functionality and coordination for managing scoreboards and viewers.
         * @throws IllegalStateException if the Manager instance has already been set.
         */
        @JvmStatic
        public fun <V : Any> setInstance(instance: Manager<V>) {
            check(this.instance == null) { "Manager instance has already been set." }
            this.instance = instance
        }

        /**
         * Retrieves the singleton instance of the Manager class.
         * This method ensures that an instance has been set before returning it,
         * throwing an exception if the instance is not yet initialized.
         *
         * @return The singleton instance of Manager.
         * @throws IllegalStateException if the instance has not been initialized.
         */
        @JvmStatic
        public fun <V : Any> getInstance(): Manager<V> {
            check(instance != null) { "Manager instance has not been set yet." }
            @Suppress("UNCHECKED_CAST")
            return instance as Manager<V>
        }
    }

    /**
     * Represents the platform object associated with the Manager.
     * The platform provides methods and properties to interact with
     * and manage the platform-specific resources, such as scoreboards
     * and player interactions.
     *
     * @param V The type of the platform-specific player object.
     */
    public val platform: Platform<V>

    /**
     * Holds the primary configuration for the platform, encapsulating various settings
     * and parameters required for operation, including language settings, versioning,
     * update checks, and specific configurations for conditions and scoreboards.
     *
     * @param V The type of the platform-specific player object.
     */
    public val config: MainConfig<V>

    /**
     * Provides access to the internationalization (i18n) component within the Manager.
     * This property allows for the translation of strings into different languages,
     * utilizing keys and optional arguments as well as prefixes as needed.
     * It facilitates localization and ensures that messages can be easily adapted
     * for various languages and regions.
     */
    public val i18n: I18n

    /**
     * A functional interface representing a mechanism for replacing variables in a text string
     * with corresponding values for a given viewer.
     *
     * The `varReplacer` property is used to transform specific placeholders within text with
     * actual values derived from the context of the viewer.
     *
     * @receiver A Manager instance which manages the resources related to scoreboards and viewers.
     * @param V The type of the platform-specific player object.
     * @return An implementation of `VarReplacer` interface capable of executing variable replacement
     * logic in text strings.
     */
    public val varReplacer: VarReplacer<V>

    /**
     * Loads the current configuration settings into the system. This method is
     * responsible for initializing and updating the necessary fields, resources,
     * and state within the application according to the latest configuration
     * data available. It ensures that the system components operate based on
     * consistent and updated configuration values.
     */
    public fun loadConfiguration()

    /**
     * Represents the collection of scoreboards managed by the system.
     * Each scoreboard within the list is associated with a specific viewer type `V`.
     * This property provides centralized access to all currently managed scoreboards.
     */
    public val scoreboards: List<Scoreboard<V>>

    /**
     * Retrieves a scoreboard by its name.
     *
     * @param name The name of the scoreboard to be retrieved.
     * @return The scoreboard associated with the given name, or null if no scoreboard with that name exists.
     */
    public fun getScoreboard(name: String): Scoreboard<V>?

    /**
     * Adds a new scoreboard to the manager.
     *
     * @param scoreboard The scoreboard to be added.
     * @return The added scoreboard if successful, or null if a scoreboard with the same name already exists.
     */
    public fun addScoreboard(scoreboard: Scoreboard<V>): Scoreboard<V>?

    /**
     * Removes a specified scoreboard from the manager.
     *
     * @param scoreboard The scoreboard to be removed.
     * @return The removed scoreboard if successful, or null if the scoreboard was not found.
     */
    public fun removeScoreboard(scoreboard: Scoreboard<V>): Scoreboard<V>?

    /**
     * A list that holds the current viewers within the manager context.
     * Each viewer is associated with platform-specific player interaction
     * capabilities and allows for management of scoreboard visibility and updates.
     *
     * @param V The type representing the platform-specific player object.
     */
    public val viewers: List<Viewer<V>>

    /**
     * Retrieves a viewer associated with the given unique identifier.
     *
     * @param uniqueID The unique identifier used to locate the viewer.
     * @return The viewer associated with the provided unique identifier, or null if no viewer is found.
     */
    public fun getViewer(uniqueID: UUID): Viewer<V>?
}