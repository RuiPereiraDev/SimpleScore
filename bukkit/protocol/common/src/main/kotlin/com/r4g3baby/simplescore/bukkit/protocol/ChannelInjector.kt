package com.r4g3baby.simplescore.bukkit.protocol

import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection
import io.netty.channel.Channel
import org.bukkit.entity.Player
import java.util.Collections.synchronizedMap
import java.util.WeakHashMap

class ChannelInjector {
    companion object {
        private val getPlayerHandle: Reflection.MethodInvoker
        private val connectionField: Reflection.FieldAccessor
        private val networkManagerField: Reflection.FieldAccessor
        private val channelField: Reflection.FieldAccessor

        private val channelLookup: MutableMap<Player, Channel> = synchronizedMap(WeakHashMap())

        init {
            try {
                val craftPlayer = Reflection.getClass("${Utils.OBC}.entity.CraftPlayer")
                val entityPlayer = Reflection.findClass(
                    "net.minecraft.server.level.ServerPlayer",
                    "net.minecraft.server.level.EntityPlayer", "${Utils.NMS}.EntityPlayer"
                )
                val playerConnection = Reflection.findClass(
                    "net.minecraft.server.network.ServerGamePacketListenerImpl",
                    "net.minecraft.server.network.PlayerConnection", "${Utils.NMS}.PlayerConnection"
                )
                val networkManager = Reflection.findClass(
                    "net.minecraft.network.Connection",
                    "net.minecraft.network.NetworkManager", "${Utils.NMS}.NetworkManager"
                )

                getPlayerHandle = Reflection.getMethodByName(craftPlayer, "getHandle")
                connectionField = Reflection.getField(entityPlayer, playerConnection)
                networkManagerField = Reflection.getField(playerConnection, networkManager)
                channelField = Reflection.getField(networkManager, Channel::class.java)
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }

        fun getChannel(player: Player): Channel {
            var channel = channelLookup[player]

            if (channel == null || !channel.isOpen) {
                val connection = connectionField.get(getPlayerHandle.invoke(player))
                val networkManager = networkManagerField.get(connection)

                channel = channelField.get(networkManager) as Channel
                channelLookup[player] = channel
            }

            return channel
        }

        fun Channel.writePacket(packet: WrappedPacket) = writePackets(packet)
        fun Channel.writePackets(vararg packets: WrappedPacket) {
            for (packet in packets) {
                writeAndFlush(packet.handle)
            }
        }
    }
}
