package com.r4g3baby.simplescore.bukkit.protocol.util

import org.bukkit.Bukkit
import java.util.regex.Pattern

data class ServerVersion(
    val major: Int,
    val minor: Int,
    val build: Int
) : Comparable<ServerVersion> {
    companion object {
        val currentVersion: ServerVersion

        // https://minecraft.wiki/w/Java_Edition_version_history
        val bountifulUpdate = ServerVersion("1.8")
        val combatUpdate = ServerVersion("1.9")
        val frostburnUpdate = ServerVersion("1.10")
        val explorationUpdate = ServerVersion("1.11")
        val worldOfColorUpdate = ServerVersion("1.12")
        val aquaticUpdate = ServerVersion("1.13")
        val villageAndPillageUpdate = ServerVersion("1.14")
        val buzzyBeesUpdate = ServerVersion("1.15")
        val netherUpdate = ServerVersion("1.16")
        val cavesAndCliffsPartIUpdate = ServerVersion("1.17")
        val cavesAndCliffsPartIIUpdate = ServerVersion("1.18")
        val theWildUpdate = ServerVersion("1.19")
        val trailsAndTailsUpdate = ServerVersion("1.20")
        val trickyTrialsUpdate = ServerVersion("1.21")

        private val versionPattern = Pattern.compile(".*\\(.*MC.\\s*([a-zA-Z0-9\\-.]+).*")

        init {
            val serverVersion = Bukkit.getVersion()
            val version = versionPattern.matcher(serverVersion)
            if (version.matches() && version.group(1) != null) {
                currentVersion = ServerVersion(version.group(1))
            } else throw IllegalStateException("Cannot parse version '$serverVersion'")
        }

        operator fun invoke(version: String): ServerVersion {
            val elements = version.split(".")
            check(elements.isNotEmpty()) { "Cannot parse version '$version'" }

            val numbers = IntArray(3)
            for (i in 0 until numbers.size.coerceAtMost(elements.size)) {
                numbers[i] = elements[i].trim().toInt()
            }

            return ServerVersion(numbers[0], numbers[1], numbers[2])
        }

        fun isBellow(version: ServerVersion): Boolean {
            return currentVersion.isBellow(version)
        }

        fun atOrBellow(version: ServerVersion): Boolean {
            return currentVersion.atOrBellow(version)
        }

        fun isAbove(version: ServerVersion): Boolean {
            return currentVersion.isAbove(version)
        }

        fun atOrAbove(version: ServerVersion): Boolean {
            return currentVersion.atOrAbove(version)
        }
    }

    fun isBellow(version: ServerVersion): Boolean {
        return compareTo(version) < 0
    }

    fun atOrBellow(version: ServerVersion): Boolean {
        return compareTo(version) <= 0
    }

    fun isAbove(version: ServerVersion): Boolean {
        return compareTo(version) > 0
    }

    fun atOrAbove(version: ServerVersion): Boolean {
        return compareTo(version) >= 0
    }

    override fun compareTo(other: ServerVersion): Int {
        major.compareTo(other.major).takeIf { it != 0 }?.let { return it }
        minor.compareTo(other.minor).takeIf { it != 0 }?.let { return it }
        build.compareTo(other.build).takeIf { it != 0 }?.let { return it }
        return 0
    }
}