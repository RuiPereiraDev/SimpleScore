package com.r4g3baby.simplescore.bukkit.protocol

import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveScore
import com.r4g3baby.simplescore.bukkit.protocol.model.ObjectiveTitle
import com.r4g3baby.simplescore.bukkit.protocol.model.PlayerObjective
import org.bukkit.ChatColor
import org.bukkit.ChatColor.COLOR_CHAR
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class ProtocolHandler {
    protected val playerObjectives = ConcurrentHashMap<UUID, PlayerObjective>()

    fun getObjective(player: Player): PlayerObjective? {
        return playerObjectives[player.uniqueId]
    }

    abstract fun createObjective(player: Player, title: ObjectiveTitle): PlayerObjective
    abstract fun removeObjective(player: Player): PlayerObjective?
    abstract fun updateScoreboard(player: Player, title: ObjectiveTitle, scores: Map<String, ObjectiveScore>)

    protected fun getObjectiveName(player: Player): String {
        return "sb${player.uniqueId.toString().replace("-", "")}".substring(0..15)
    }

    protected fun identifierToName(identifier: String): String {
        return identifier.toCharArray().joinToString(COLOR_CHAR.toString(), COLOR_CHAR.toString(), "${COLOR_CHAR}r")
    }

    protected fun splitScoreLine(text: String, cutSuffix: Boolean = false): Pair<String, String> {
        var index = 16
        if (text.length > index) {
            // Prevent splitting normal color codes
            if (text.elementAt(index - 1) == COLOR_CHAR) index--

            // Prevent splitting hex color codes
            for (i in 1..6) {
                val newIndex = index - (i * 2)

                // This isn't a hex color code
                if (text.elementAt(newIndex) != COLOR_CHAR) break

                // Found start of hex color code
                if (text.elementAt(newIndex + 1) == 'x') {
                    index = newIndex
                    break
                }
            }

            val prefix = text.take(index)
            val lastColors = ChatColor.getLastColors(prefix)

            var suffix = lastColors + text.substring(index)
            if (cutSuffix && suffix.length > 16) {
                suffix = suffix.take(16)
            }

            return prefix to suffix
        }
        return text to ""
    }
}