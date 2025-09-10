package com.r4g3baby.simplescore.bukkit.protocol.modern.chat

import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection

class WrappedChatComponent(val handle: Any) {
    companion object {
        val clazz: Class<*>

        private val fromString: Reflection.MethodInvoker
        //private val fromJsonElement: Reflection.MethodInvoker

        init {
            try {
                clazz = Reflection.findClass(
                    "net.minecraft.network.chat.Component",
                    "net.minecraft.network.chat.IChatBaseComponent",
                    "${Utils.NMS}.IChatBaseComponent"
                )

                val craftChatMessage = Reflection.getClass("${Utils.OBC}.util.CraftChatMessage")
                val arrayOfIChatBaseComponent = Reflection.getArrayClass(clazz)
                /*val chatSerializer = Reflection.findClass(
                    "net.minecraft.network.chat.IChatBaseComponent\$ChatSerializer",
                    "${Reflection.NMS}.IChatBaseComponent\$ChatSerializer"
                )

                val returnType = if (ServerVersion.isAbove(ServerVersion("1.16.5"))) {
                    Reflection.getClass("net.minecraft.network.chat.IChatMutableComponent")
                } else clazz*/

                fromString = Reflection.getMethod(craftChatMessage, arrayOfIChatBaseComponent, String::class.java)
                //fromJsonElement = Reflection.getMethod(chatSerializer, returnType, JsonElement::class.java)
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }

        // todo: Empty
        fun fromString(message: String): WrappedChatComponent {
            val components = fromString.invoke(null, message) as Array<*>
            return WrappedChatComponent(components[0]!!)
        }

        /*fun fromJsonElement(jsonElement: JsonElement): WrappedChatComponent {
            return WrappedChatComponent(fromJsonElement.invoke(null, jsonElement)!!)
        }*/
    }

    init {
        require(clazz.isAssignableFrom(handle::class.java))
    }
}