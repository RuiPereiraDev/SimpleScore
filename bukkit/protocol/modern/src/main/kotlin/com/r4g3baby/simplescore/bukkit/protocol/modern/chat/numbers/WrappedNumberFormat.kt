package com.r4g3baby.simplescore.bukkit.protocol.modern.chat.numbers

import com.r4g3baby.simplescore.core.util.Reflection

class WrappedNumberFormat(val handle: Any) {
    companion object {
        val clazz: Class<*>

        private val blankFormatHandle: Any?

        init {
            try {
                clazz = Reflection.getClass("net.minecraft.network.chat.numbers.NumberFormat")

                val blankFormat = Reflection.getClass("net.minecraft.network.chat.numbers.BlankFormat")

                blankFormatHandle = Reflection.getField(blankFormat, blankFormat).get(null)
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }

        val blankFormat = if (blankFormatHandle != null) WrappedNumberFormat(blankFormatHandle) else null
    }

    init {
        require(clazz.isAssignableFrom(handle::class.java))
    }
}