package com.r4g3baby.simplescore.core.i18n

import com.r4g3baby.simplescore.api.Platform
import com.r4g3baby.simplescore.api.i18n.I18n
import com.r4g3baby.simplescore.core.util.translateColorCodes
import java.io.File
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.MessageFormat
import java.util.*

class I18n<V : Any>(private val platform: Platform<V>) : I18n {
    private lateinit var defaultBundle: ResourceBundle
    private lateinit var customBundle: ResourceBundle

    private val messageFormatCache = HashMap<String, MessageFormat>()

    override fun trans(key: String, vararg args: Any, prefixed: Boolean): String {
        val prefix = if (prefixed) {
            trans("prefix", prefixed = false)
        } else null

        val translation = translateColorCodes(
            buildString {
                if (prefix?.isNotBlank() == true) append(prefix, " ")

                try {
                    try {
                        append(customBundle.getString(key))
                    } catch (_: MissingResourceException) {
                        append(defaultBundle.getString(key))
                    }
                } catch (_: MissingResourceException) {
                    platform.getLogger().warning("Missing translation key: $key.")
                }
            }
        )

        if (args.isEmpty()) return translation
        return messageFormatCache.computeIfAbsent(translation) {
            MessageFormat(translation)
        }.format(args)
    }

    fun loadTranslations(language: String) {
        val parts = language.split("_", "-", ".", "\\", "/")
        val locale = when (parts.size) {
            1 -> Locale(parts[0])
            2 -> Locale(parts[0], parts[1])
            3 -> Locale(parts[0], parts[1], parts[2])
            else -> Locale(language)
        }

        ResourceBundle.clearCache()

        defaultBundle = try {
            ResourceBundle.getBundle("lang/messages", locale, UTF8PropertiesControl)
        } catch (_: MissingResourceException) {
            NullBundle
        }

        customBundle = try {
            ResourceBundle.getBundle("messages", locale, PluginResClassLoader(platform), UTF8PropertiesControl)
        } catch (_: MissingResourceException) {
            NullBundle
        }
    }

    private class PluginResClassLoader<V : Any>(platform: Platform<V>) : ClassLoader(platform.javaClass.classLoader) {
        private val dataFolder = platform.getDataFolder()

        override fun getResource(name: String): URL? {
            val file = File(dataFolder, name)
            if (file.exists()) {
                try {
                    return file.toURI().toURL()
                } catch (_: MalformedURLException) {
                }
            }
            return null
        }

        override fun getResourceAsStream(name: String): InputStream? {
            val file = File(dataFolder, name)
            if (file.exists()) {
                return file.inputStream()
            }
            return null
        }
    }

    private object UTF8PropertiesControl : ResourceBundle.Control() {
        override fun newBundle(
            baseName: String?, locale: Locale?, format: String?, loader: ClassLoader?, reload: Boolean
        ): ResourceBundle? {
            val resourceName = toResourceName(toBundleName(baseName, locale), "properties")

            var stream: InputStream? = null
            if (reload) {
                val url = loader?.getResource(resourceName)
                if (url != null) {
                    val connection = url.openConnection()
                    if (connection != null) {
                        connection.useCaches = false
                        stream = connection.getInputStream()
                    }
                }
            } else stream = loader?.getResourceAsStream(resourceName)

            stream?.use {
                return PropertyResourceBundle(it.reader())
            }
            return null
        }

        override fun toString(): String {
            return "UTF8PropertiesControl"
        }
    }

    private data object NullBundle : ResourceBundle() {
        override fun handleGetObject(key: String): Any? {
            return null
        }

        override fun getKeys(): Enumeration<String> {
            return Collections.emptyEnumeration()
        }
    }
}