package com.r4g3baby.simplescore.bukkit.protocol.legacy.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection

data class WrappedUpdateScore(
    val objectiveName: String,
    val entityName: String,
    val action: Action,
    val value: Int? = null
) : WrappedPacket(packetPlayOutScoreboardScore.newInstance()) {
    private companion object {
        private val packetPlayOutScoreboardScore: Class<*>

        private val objectiveNameField: Reflection.FieldAccessor
        private val entityNameField: Reflection.FieldAccessor
        private val actionField: Reflection.FieldAccessor
        private val actionFieldEnums: Array<out Any>
        private val valueField: Reflection.FieldAccessor

        init {
            try {
                packetPlayOutScoreboardScore = Reflection.getClass(
                    "${Utils.NMS}.PacketPlayOutScoreboardScore"
                )

                val enumScoreboardAction = Reflection.getClass(
                    "${Utils.NMS}.PacketPlayOutScoreboardScore\$EnumScoreboardAction"
                )

                objectiveNameField = Reflection.getField(packetPlayOutScoreboardScore, String::class.java, 1)
                entityNameField = Reflection.getField(packetPlayOutScoreboardScore, String::class.java, 0)
                actionField = Reflection.getField(packetPlayOutScoreboardScore, enumScoreboardAction)
                actionFieldEnums = enumScoreboardAction.enumConstants
                valueField = Reflection.getField(packetPlayOutScoreboardScore, Int::class.java)
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }
    }

    enum class Action(val id: Int) {
        UPDATE(0), REMOVE(1);
    }

    init {
        objectiveNameField.set(handle, objectiveName)
        entityNameField.set(handle, entityName)
        actionField.set(handle, actionFieldEnums[action.id])
        if (value != null) valueField.set(handle, value)
    }
}
