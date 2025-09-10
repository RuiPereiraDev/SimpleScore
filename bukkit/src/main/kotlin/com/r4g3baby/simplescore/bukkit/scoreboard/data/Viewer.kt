package com.r4g3baby.simplescore.bukkit.scoreboard.data

import com.r4g3baby.simplescore.api.scoreboard.Scoreboard
import com.r4g3baby.simplescore.api.scoreboard.data.Priority
import com.r4g3baby.simplescore.api.scoreboard.data.Provider
import com.r4g3baby.simplescore.api.scoreboard.data.Viewer
import org.bukkit.entity.Player
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

data class Viewer(
    override val reference: WeakReference<Player>,
) : Viewer<Player> {
    constructor(player: Player) : this(WeakReference(player))

    private data class ScoreboardEntry(
        val scoreboard: Scoreboard<Player>?, val priority: Priority
    )

    private val entries = ConcurrentHashMap<Provider, ScoreboardEntry>()
    private val hiding = CopyOnWriteArrayList<Provider>()

    private var cachedScoreboard: Scoreboard<Player>? = null
    private var isCacheDirty: Boolean = true

    override val scoreboard: Scoreboard<Player>?
        get() {
            if (isCacheDirty) {
                cachedScoreboard = entries.entries.maxByOrNull { it.value.priority }?.value?.scoreboard
                isCacheDirty = false
            }
            return cachedScoreboard
        }
    override val isScoreboardHidden: Boolean
        get() = hiding.isNotEmpty()

    override fun setScoreboard(scoreboard: Scoreboard<Player>?, provider: Provider, priority: Priority) {
        entries[provider] = ScoreboardEntry(scoreboard, priority)
        invalidateCache()
    }

    override fun getScoreboard(provider: Provider): Scoreboard<Player>? {
        return entries[provider]?.scoreboard
    }

    override fun removeScoreboard(provider: Provider): Scoreboard<Player>? {
        val removed = entries.remove(provider)
        if (removed != null) invalidateCache()
        return removed?.scoreboard
    }

    override fun setPriority(priority: Priority?, provider: Provider) {
        entries.computeIfPresent(provider) { _, entry ->
            entry.copy(priority = priority ?: Priority.Normal)
        }
        invalidateCache()
    }

    override fun getPriority(provider: Provider): Priority? {
        return entries[provider]?.priority
    }

    override fun hideScoreboard(provider: Provider): Boolean {
        return hiding.addIfAbsent(provider)
    }

    override fun showScoreboard(provider: Provider): Boolean {
        return hiding.remove(provider)
    }

    override fun isHidingScoreboard(provider: Provider): Boolean {
        return hiding.contains(provider)
    }

    private fun invalidateCache() {
        isCacheDirty = true
    }
}