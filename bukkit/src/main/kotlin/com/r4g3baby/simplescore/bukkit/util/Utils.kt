package com.r4g3baby.simplescore.bukkit.util

import com.r4g3baby.simplescore.api.scoreboard.data.Provider
import com.r4g3baby.simplescore.core.util.Reflection
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.objenesis.ObjenesisStd
import org.objenesis.instantiator.ObjectInstantiator
import java.util.function.Function

val OBC: String = Bukkit.getServer().javaClass.getPackage().name
val NMS: String = OBC.replace("org.bukkit.craftbukkit", "net.minecraft.server")

private val objenesis = ObjenesisStd(true)
fun <T> getInstantiatorOf(clazz: Class<T>): ObjectInstantiator<T> {
    return objenesis.getInstantiatorOf<T>(clazz)
}

fun String.lazyReplace(oldValue: String, newValueFunc: () -> String): String {
    if (oldValue.isEmpty()) return this
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
                val craftPlayer = Reflection.getClass("${OBC}.entity.CraftPlayer")
                Reflection.getMethodByName(craftPlayer, "getHandle")
            } catch (_: Exception) { null }

            pingField = try {
                val entityPlayer = Reflection.findClass(
                    "net.minecraft.server.level.ServerPlayer",
                    "net.minecraft.server.level.EntityPlayer", "${NMS}.EntityPlayer"
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
