package com.r4g3baby.simplescore.bukkit.protocol

import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection

abstract class WrappedPacket(val handle: Any) {
    companion object {
        val clazz: Class<*>

        init {
            try {
                clazz = Reflection.findClass(
                    "net.minecraft.network.protocol.Packet", "${Utils.NMS}.Packet"
                )
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }
    }

    init {
        require(clazz.isAssignableFrom(handle::class.java))
    }
}