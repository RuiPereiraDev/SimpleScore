package com.r4g3baby.simplescore.bukkit.protocol.modern.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection
import org.objenesis.instantiator.ObjectInstantiator

data class WrappedResetScore(
    val entityName: String,
    val objectiveName: String? = null
) : WrappedPacket(packetInstantiator.newInstance()) {
    private companion object {
        private val packetInstantiator: ObjectInstantiator<out Any>

        private val entityNameField: Reflection.FieldAccessor
        private val objectiveNameField: Reflection.FieldAccessor

        init {
            try {
                val clientboundResetScorePacket = Reflection.getClass(
                    "net.minecraft.network.protocol.game.ClientboundResetScorePacket"
                )
                packetInstantiator = Utils.getInstantiatorOf(clientboundResetScorePacket)

                entityNameField = Reflection.getField(clientboundResetScorePacket, String::class.java, 0)
                objectiveNameField = Reflection.getField(clientboundResetScorePacket, String::class.java, 1)
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }
    }

    init {
        entityNameField.set(handle, entityName)
        objectiveNameField.set(handle, objectiveName)
    }
}
