package com.r4g3baby.simplescore.bukkit.protocol

import com.r4g3baby.simplescore.bukkit.protocol.Testing.Companion.writeString
import com.r4g3baby.simplescore.bukkit.protocol.Testing.Companion.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

data class UpdateObjective(
    val name: String,
    val mode: Mode,
    val value: String?
) : Packet {
    enum class Mode(
        val id: Int
    ) {
        CREATE(0),
        REMOVE(1),
        UPDATE(2)
    }

    override fun asBuffer(): ByteBuf {
        return Unpooled.buffer().apply {
            writeVarInt(0x5A) // Packet ID - Update Objectives
            writeString(name) // Objective Name
            writeByte(mode.id) // Mode
            if ((mode == Mode.CREATE || mode == Mode.UPDATE)) {
                writeString(value ?: "") // Objective Value
                writeVarInt(0) // Type
            }
        }
    }
}