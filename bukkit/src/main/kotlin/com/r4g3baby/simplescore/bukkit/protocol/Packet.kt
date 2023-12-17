package com.r4g3baby.simplescore.bukkit.protocol

import io.netty.buffer.ByteBuf

interface Packet {
    fun asBuffer(): ByteBuf
}