package com.r4g3baby.simplescore.bukkit.protocol.legacy.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection

data class WrappedDisplayObjective(
    val objectiveName: String,
    val position: Position
) : WrappedPacket(packetPlayOutScoreboardDisplayObjective.newInstance()) {
    private companion object {
        private val packetPlayOutScoreboardDisplayObjective: Class<*>

        private val objectiveNameField: Reflection.FieldAccessor
        private val displaySlotField: Reflection.FieldAccessor

        init {
            try {
                packetPlayOutScoreboardDisplayObjective = Reflection.getClass(
                    "${Utils.NMS}.PacketPlayOutScoreboardDisplayObjective"
                )

                objectiveNameField = Reflection.getField(packetPlayOutScoreboardDisplayObjective, String::class.java)
                displaySlotField = Reflection.getField(packetPlayOutScoreboardDisplayObjective, Int::class.java)
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }
    }

    enum class Position(val id: Int) {
        LIST(0), SIDEBAR(1), BELLOW_NAME(2);
    }

    init {
        objectiveNameField.set(handle, objectiveName)
        displaySlotField.set(handle, position.id)
    }
}
