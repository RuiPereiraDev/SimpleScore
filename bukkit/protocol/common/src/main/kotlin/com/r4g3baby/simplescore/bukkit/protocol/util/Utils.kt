package com.r4g3baby.simplescore.bukkit.protocol.util

import org.bukkit.Bukkit
import org.objenesis.ObjenesisStd
import org.objenesis.instantiator.ObjectInstantiator

object Utils {
    val OBC: String = Bukkit.getServer().javaClass.getPackage().name
    val NMS: String = OBC.replace("org.bukkit.craftbukkit", "net.minecraft.server")

    private val objenesis = ObjenesisStd(true)
    fun <T> getInstantiatorOf(clazz: Class<T>): ObjectInstantiator<T> {
        return objenesis.getInstantiatorOf<T>(clazz)
    }
}