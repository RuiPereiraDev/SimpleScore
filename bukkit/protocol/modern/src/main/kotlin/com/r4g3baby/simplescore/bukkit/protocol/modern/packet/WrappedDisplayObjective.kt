package com.r4g3baby.simplescore.bukkit.protocol.modern.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.util.ServerVersion
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection
import org.objenesis.instantiator.ObjectInstantiator

data class WrappedDisplayObjective(
    val objectiveName: String,
    val position: Position
) : WrappedPacket(packetInstantiator.newInstance()) {
    private companion object {
        private val packetInstantiator: ObjectInstantiator<out Any>

        private val objectiveNameField: Reflection.FieldAccessor
        private val displaySlotField: Reflection.FieldAccessor
        private val displaySlotFieldEnums: Array<out Any>

        private val afterTrailsAndTailsUpdate3 = ServerVersion.atOrAbove(ServerVersion("1.20.3"))

        init {
            try {
                val packetPlayOutScoreboardDisplayObjective = Reflection.findClass(
                    "net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket",
                    "net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective",
                    "${Utils.NMS}.PacketPlayOutScoreboardDisplayObjective"
                )
                packetInstantiator = Utils.getInstantiatorOf(packetPlayOutScoreboardDisplayObjective)

                objectiveNameField = Reflection.getField(packetPlayOutScoreboardDisplayObjective, String::class.java)

                if (afterTrailsAndTailsUpdate3) {
                    val displaySlot = Reflection.getClass("net.minecraft.world.scores.DisplaySlot")

                    displaySlotField = Reflection.getField(packetPlayOutScoreboardDisplayObjective, displaySlot)
                    displaySlotFieldEnums = displaySlot.enumConstants
                } else {
                    displaySlotField = Reflection.getField(packetPlayOutScoreboardDisplayObjective, Int::class.java)
                    displaySlotFieldEnums = emptyArray()
                }
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

        if (afterTrailsAndTailsUpdate3) {
            displaySlotField.set(handle, displaySlotFieldEnums[position.id])
        } else displaySlotField.set(handle, position.id)
    }
}