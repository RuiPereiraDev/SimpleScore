package com.r4g3baby.simplescore.bukkit.protocol.modern.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.WrappedChatComponent
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.numbers.WrappedNumberFormat
import com.r4g3baby.simplescore.bukkit.protocol.util.ServerVersion
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection
import org.objenesis.instantiator.ObjectInstantiator
import java.util.Optional

data class WrappedUpdateScore(
    val entityName: String,
    val objectiveName: String,
    val action: Action,
    val value: Int? = null,
    val displayName: WrappedChatComponent? = null,
    val numberFormat: WrappedNumberFormat? = null
) : WrappedPacket(packetInstantiator.newInstance()) {
    private companion object {
        private val packetInstantiator: ObjectInstantiator<out Any>

        private val entityNameField: Reflection.FieldAccessor
        private val objectiveNameField: Reflection.FieldAccessor
        private val valueField: Reflection.FieldAccessor
        private val actionField: Reflection.FieldAccessor?
        private val actionFieldEnums: Array<out Any>
        private val displayNameField: Reflection.FieldAccessor?
        private val numberFormatField: Reflection.FieldAccessor?

        private val trailsAndTailsUpdate6 = ServerVersion.trailsAndTailsUpdate.copy(build = 6)
        private val afterTrailsAndTailsUpdate6 = ServerVersion.atOrAbove(trailsAndTailsUpdate6)

        init {
            try {
                val packetPlayOutScoreboardScore = Reflection.findClass(
                    "net.minecraft.network.protocol.game.ClientboundSetScorePacket",
                    "net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore",
                    "${Utils.NMS}.PacketPlayOutScoreboardScore"
                )
                packetInstantiator = Utils.getInstantiatorOf(packetPlayOutScoreboardScore)

                entityNameField = Reflection.getField(packetPlayOutScoreboardScore, String::class.java, 0)
                objectiveNameField = Reflection.getField(packetPlayOutScoreboardScore, String::class.java, 1)
                valueField = Reflection.getField(packetPlayOutScoreboardScore, Int::class.java)

                if (ServerVersion.atOrAbove(ServerVersion("1.20.3"))) {
                    actionField = null
                    actionFieldEnums = emptyArray()
                    if (afterTrailsAndTailsUpdate6) {
                        displayNameField = Reflection.getField(packetPlayOutScoreboardScore, Optional::class.java, 0)
                        numberFormatField = Reflection.getField(packetPlayOutScoreboardScore, Optional::class.java, 1)
                    } else {
                        displayNameField = Reflection.getField(packetPlayOutScoreboardScore, WrappedChatComponent.clazz)
                        numberFormatField = Reflection.getField(packetPlayOutScoreboardScore, WrappedNumberFormat.clazz)
                    }
                } else {
                    val enumAction = Reflection.findClass(
                        $$"net.minecraft.server.ServerScoreboard$Method",
                        $$"net.minecraft.server.ScoreboardServer$Action", $$"$${Utils.NMS}.ScoreboardServer$Action"
                    )

                    actionField = Reflection.getField(packetPlayOutScoreboardScore, enumAction)
                    actionFieldEnums = enumAction.enumConstants
                    displayNameField = null
                    numberFormatField = null
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                throw ExceptionInInitializerError(throwable)
            }
        }
    }

    enum class Action(val id: Int) {
        UPDATE(0), REMOVE(1);
    }

    init {
        entityNameField.set(handle, entityName)
        objectiveNameField.set(handle, objectiveName)
        actionField?.set(handle, actionFieldEnums[action.id])
        if (value != null) valueField.set(handle, value)

        if (afterTrailsAndTailsUpdate6) {
            displayNameField?.set(handle, Optional.ofNullable(displayName?.handle))
            numberFormatField?.set(handle, Optional.ofNullable(numberFormat?.handle))
        } else {
            if (displayName != null) displayNameField?.set(handle, displayName.handle)
            if (numberFormat != null) numberFormatField?.set(handle, numberFormat.handle)
        }
    }
}
