package com.r4g3baby.simplescore.api.scoreboard

/**
 * Functional interface for replacing placeholders or variables within a given text.
 *
 * @param V The type of the platform-specific player object.
 */
public fun interface VarReplacer<V : Any> {
    /**
     * Replaces placeholders or variables within the given text using the provided viewer.
     *
     * @param text The original text containing placeholders or variables to be replaced.
     * @param viewer The platform-specific player object used to determine replacement values.
     * @return A new string with placeholders or variables replaced based on the viewer.
     */
    public fun replace(text: String, viewer: V): String
}