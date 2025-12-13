package com.r4g3baby.simplescore.api.i18n

/**
 * An interface that defines internationalization (i18n) functionalities for translating keys into
 * language-specific strings. This interface provides methods to support localization, allowing
 * dynamic translation of strings using keys and optional arguments.
 */
public interface I18n {
    /**
     * Translates a key into the appropriate language-specific string with optional arguments for formatting.
     *
     * @param key The key to be translated.
     * @param args Optional arguments to be inserted into the translated string.
     * @param prefixed Flag indicating whether the key should be prefixed.
     * @return The translated string with applied formatting.
     */
    public fun t(key: String, vararg args: Any, prefixed: Boolean = true): String = trans(key, *args, prefixed = prefixed)

    /**
     * Translates a key into the appropriate language-specific string with optional arguments for formatting.
     *
     * @param key The key to be translated.
     * @param args Optional arguments to be inserted into the translated string.
     * @param prefixed Flag indicating whether the key should be prefixed.
     * @return The translated string with applied formatting.
     */
    public fun trans(key: String, vararg args: Any, prefixed: Boolean = true): String
}
