package com.r4g3baby.simplescore.bukkit.util

import com.r4g3baby.simplescore.api.scoreboard.data.Provider
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.function.Function

fun String.lazyReplace(oldValue: String, newValueFunc: () -> String): String {
    var occurrenceIndex = this.indexOf(oldValue, ignoreCase = true)
    if (occurrenceIndex < 0) return this

    val newValue = newValueFunc()
    val oldValueLength = oldValue.length

    val newLengthHint = this.length - oldValueLength + newValue.length
    if (newLengthHint < 0) throw OutOfMemoryError()

    var cursor = 0
    val stringBuilder = StringBuilder(newLengthHint)
    while (occurrenceIndex >= 0) {
        stringBuilder.append(this, cursor, occurrenceIndex).append(newValue)

        cursor = occurrenceIndex + oldValueLength
        occurrenceIndex = this.indexOf(oldValue, cursor, ignoreCase = true)
    }

    if (cursor < this.length) {
        stringBuilder.append(this, cursor, this.length)
    }

    return stringBuilder.toString()
}

fun bukkitProvider(plugin: Plugin): Provider {
    return Provider(plugin.name)
}

@JvmField
val getPlayerPing = object : Function<Player, Int> {
    private val getPingMethod: Reflection.MethodInvoker? = try {
        Reflection.getMethodByName(Player::class.java, "getPing")
    } catch (_: Exception) { null }

    private val getPlayerHandle: Reflection.MethodInvoker?
    private val pingField: Reflection.FieldAccessor?

    init {
        if (getPingMethod == null) {
            getPlayerHandle = try {
                val craftPlayer = Reflection.getClass("${Utils.OBC}.entity.CraftPlayer")
                Reflection.getMethodByName(craftPlayer, "getHandle")
            } catch (_: Exception) { null }

            pingField = try {
                val entityPlayer = Reflection.findClass(
                    "net.minecraft.server.level.ServerPlayer",
                    "net.minecraft.server.level.EntityPlayer", "${Utils.NMS}.EntityPlayer"
                )
                Reflection.getField(entityPlayer, Int::class.java, filter = { field -> field.name == "ping" })
            } catch (_: Exception) { null }
        } else {
            getPlayerHandle = null
            pingField = null
        }
    }

    override fun apply(player: Player): Int {
        return when {
            getPingMethod != null -> getPingMethod.invoke(player) as? Int ?: -1
            getPlayerHandle != null && pingField != null -> pingField.get(getPlayerHandle.invoke(player)) as? Int ?: -1
            else -> -1
        }
    }
}
