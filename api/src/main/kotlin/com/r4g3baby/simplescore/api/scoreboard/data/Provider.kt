package com.r4g3baby.simplescore.api.scoreboard.data

/**
 * A data class representing a scoreboard provider that manages scoreboards within the system.
 * Each provider is uniquely identified by a combination of a plugin name and an optional context.
 *
 * Key features:
 * - Provides unique identification for scoreboard management
 * - Supports contextual subdivisions through optional context parameter
 * - Enables hierarchical organization of scoreboards
 * - Maintains immutability for thread safety
 *
 * Example usage:
 * ```kotlin
 * // Basic provider for a plugin
 * val mainProvider = Provider("MyPlugin")
 *
 * // Providers for specific contexts
 * val lobbyProvider = Provider("MyPlugin", "lobby")
 * val gameProvider = Provider("MyPlugin", "game")
 *
 * // Creating related providers using withContext
 * val baseProvider = Provider("MyPlugin")
 * val arenaProvider = baseProvider.withContext("arena")
 * ```
 *
 * @property plugin The name of the plugin that owns this provider
 * @property context Optional contextual identifier for creating subdivisions within a plugin's namespace
 */
public data class Provider(
    val plugin: String,
    val context: String? = null
) {
    /**
     * A unique identifier for this provider, combining the plugin name and optional context.
     * Format: "pluginName" or "pluginName/context" when context is provided.
     */
    public val name: String = if (context != null) "$plugin/$context" else plugin

    /**
     * Creates a new Provider instance with the same plugin name but a different context.
     * This method is useful for creating related providers for different features or
     * aspects of the same plugin.
     *
     * Example:
     * ```kotlin
     * val baseProvider = Provider("MyPlugin")
     * val lobbyProvider = baseProvider.withContext("lobby")
     * val gameProvider = baseProvider.withContext("game")
     * ```
     *
     * @param context The new context to be used for the provider
     * @return A new Provider instance with the specified context
     */
    public fun withContext(context: String): Provider {
        return Provider(plugin, context)
    }
}
