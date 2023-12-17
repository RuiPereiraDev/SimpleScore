package com.r4g3baby.simplescore.bukkit.protocol

import com.r4g3baby.simplescore.bukkit.protocol.Testing.Companion.writeString
import com.r4g3baby.simplescore.bukkit.protocol.Testing.Companion.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

data class UpdateScore(
    val entityName: String,
    val action: Action,
    val objectiveName: String,
    val value: Int
) : Packet {
    enum class Action(
        val id: Int
    ) {
        CREATE(0),
        REMOVE(1),
        UPDATE(0)
    }

    override fun asBuffer(): ByteBuf {
        return Unpooled.buffer().apply {
            writeVarInt(0x5D) // Packet ID - Update Score
            writeString(entityName) // Entity Name
            writeVarInt(action.id) // Action
            writeString("testing") // Objective Name
            if (action == Action.CREATE || action == Action.UPDATE) {
                writeVarInt(value) // Value
            }
        }
    }
}
