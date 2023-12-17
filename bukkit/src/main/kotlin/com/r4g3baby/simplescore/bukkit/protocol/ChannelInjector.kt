package com.r4g3baby.simplescore.bukkit.protocol

import io.netty.channel.Channel
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import java.util.Collections.synchronizedMap

class ChannelInjector {
    companion object {
        private val getPlayerHandle: MethodHandle
        private val getConnection: MethodHandle
        private val getNetworkManager: MethodHandle
        private val getChannel: MethodHandle

        private val OBC = Bukkit.getServer().javaClass.getPackage().name
        private val NMS = OBC.replace("org.bukkit.craftbukkit", "net.minecraft.server")

        private val channelLookup: MutableMap<Player, Channel> = synchronizedMap(WeakHashMap())

        init {
            val lookup = MethodHandles.lookup()

            try {
                getPlayerHandle = lookup.unreflect(
                    getMethod("$OBC.entity.CraftPlayer", "getHandle")
                )

                val entityPlayer = getClass(
                    "net.minecraft.server.level.EntityPlayer",
                    "net.minecraft.server.level.ServerPlayer",
                    "$NMS.EntityPlayer"
                )

                val playerConnection = getClass(
                    "$NMS.PlayerConnection",
                    "net.minecraft.server.network.PlayerConnection",
                    "net.minecraft.server.network.ServerGamePacketListenerImpl"
                )

                val networkManager = getClass(
                    "$NMS.NetworkManager",
                    "net.minecraft.network.NetworkManager",
                    "net.minecraft.network.Connection"
                )

                getConnection = lookup.unreflectGetter(
                    getField(entityPlayer, playerConnection)
                )

                getNetworkManager = lookup.unreflectGetter(
                    getField(playerConnection, networkManager)
                )

                getChannel = lookup.unreflectGetter(
                    getField(networkManager, Channel::class.java)
                )
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }

        @JvmStatic
        fun getChannel(player: Player): Channel {
            var channel = channelLookup[player]

            if (channel == null || !channel.isOpen) {
                val connection = getConnection.invoke(getPlayerHandle.invoke(player))
                val networkManager = getNetworkManager.invoke(connection)

                channel = getChannel.invoke(networkManager) as Channel
                channelLookup[player] = channel
            }

            return channel
        }

        private fun getClass(className: String): Class<*> {
            try {
                return Class.forName(className)
            } catch (ex: ClassNotFoundException) {
                throw IllegalArgumentException("Cannot find class $className", ex)
            }
        }

        private fun getClass(vararg classAliases: String): Class<*> {
            for (classAlias in classAliases) {
                try {
                    return getClass(classAlias)
                } catch (ignored: IllegalArgumentException) { }
            }

            throw IllegalArgumentException("Cannot find classes $classAliases")
        }

        private fun getMethod(className: String, methodName: String): Method {
            return getMethod(getClass(className), methodName)
        }

        private fun getMethod(clazz: Class<*>, methodName: String): Method {
            for (method in clazz.declaredMethods) {
                if (method.name.equals(methodName)) {
                    method.isAccessible = true
                    return method
                }
            }

            if (clazz.superclass != null) {
                return getMethod(clazz.superclass, methodName)
            }

            throw IllegalArgumentException("Cannot find method $methodName")
        }

        private fun getField(clazz: Class<*>, fieldClass: Class<*>): Field? {
            for (field in clazz.declaredFields) {
                if (fieldClass.isAssignableFrom(field.type)) {
                    field.isAccessible = true
                    return field
                }
            }

            if (clazz.superclass != null) {
                return getField(clazz.superclass, fieldClass)
            }

            throw IllegalArgumentException("Cannot find field with type $fieldClass")
        }
    }
}