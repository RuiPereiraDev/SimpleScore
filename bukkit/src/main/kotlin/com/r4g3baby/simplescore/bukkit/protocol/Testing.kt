package com.r4g3baby.simplescore.bukkit.protocol

import com.r4g3baby.simplescore.bukkit.protocol.DisplayObjective.Position
import com.r4g3baby.simplescore.bukkit.protocol.UpdateObjective.Mode
import com.r4g3baby.simplescore.bukkit.protocol.UpdateScore.Action
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.bukkit.entity.Player
import java.nio.charset.StandardCharsets

class Testing(player: Player) {
    init {
        val buffer = Unpooled.buffer()
        buffer.writeVarInt(0x20) // Packet ID - Game Event
        buffer.writeByte(3)
        buffer.writeFloat(1F)
        ChannelInjector.getChannel(player).writeAndFlush(buffer)

        val buffer1 = Unpooled.buffer()
        buffer1.writeVarInt(0x67) // Packet ID - System Chat Message
        buffer1.writeString("{\"text\":\"Hello World!\"}")
        buffer1.writeBoolean(false)
        ChannelInjector.getChannel(player).writeAndFlush(buffer1)

        val packets = listOf(
            UpdateObjective("testing", Mode.CREATE, "{\"text\":\"Hello World!\"}"),
            DisplayObjective(Position.SIDEBAR, "testing"),
            UpdateScore("Hello World!", Action.CREATE, "testing", 1)
        )
        for (packet in packets) {
            ChannelInjector.getChannel(player).writeAndFlush(packet.asBuffer())
        }
    }

    companion object {
        fun ByteBuf.writeVarInt(value: Int) {
            var i = value
            while (true) {
                if ((i and 0x7F.inv()) == 0) {
                    writeByte(i)
                    return
                }

                writeByte((i and 0x7F) or 0x80)

                i = i ushr 7
            }
        }

        fun ByteBuf.writeString(s: String) {
            val bytes = s.toByteArray(StandardCharsets.UTF_8)
            if (bytes.size > 32767) {
                throw RuntimeException("String too big (was ${s.length} bytes encoded, max 32767)")
            } else {
                writeVarInt(bytes.size)
                writeBytes(bytes)
            }
        }
    }
}