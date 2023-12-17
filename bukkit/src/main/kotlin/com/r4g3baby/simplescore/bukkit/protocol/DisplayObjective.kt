package com.r4g3baby.simplescore.bukkit.protocol

import com.r4g3baby.simplescore.bukkit.protocol.Testing.Companion.writeString
import com.r4g3baby.simplescore.bukkit.protocol.Testing.Companion.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

data class DisplayObjective(
    val position: Position,
    val objectiveName: String
) : Packet {
    data class Position(
        val id: Int
    ) {
        companion object {
            val LIST = Position(0)
            val SIDEBAR = Position(1)
            val BELLOW_NAME = Position(2)
        }
    }

    override fun asBuffer(): ByteBuf {
        return Unpooled.buffer().apply {
            writeVarInt(0x53) // Packet ID - Display Objective
            writeVarInt(position.id) // Position
            writeString(objectiveName) // Score Name
        }
    }
}
