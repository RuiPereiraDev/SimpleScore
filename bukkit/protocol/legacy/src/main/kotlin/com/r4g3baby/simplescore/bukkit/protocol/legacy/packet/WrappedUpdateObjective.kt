package com.r4g3baby.simplescore.bukkit.protocol.legacy.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection

data class WrappedUpdateObjective(
    val name: String,
    val mode: Mode,
    val type: Type? = null,
    val displayName: String? = null,
) : WrappedPacket(packetPlayOutScoreboardObjective.newInstance()) {
    private companion object {
        private val packetPlayOutScoreboardObjective: Class<*>

        private val nameField: Reflection.FieldAccessor
        private val modeField: Reflection.FieldAccessor
        private val typeField: Reflection.FieldAccessor
        private val typeFieldEnums: Array<out Any>
        private val displayNameField: Reflection.FieldAccessor

        init {
            try {
                packetPlayOutScoreboardObjective = Reflection.getClass(
                    "${Utils.NMS}.PacketPlayOutScoreboardObjective"
                )

                val enumScoreboardHealthDisplay = Reflection.getClass(
                    $$"$${Utils.NMS}.IScoreboardCriteria$EnumScoreboardHealthDisplay"
                )

                nameField = Reflection.getField(packetPlayOutScoreboardObjective, String::class.java, 0)
                modeField = Reflection.getField(packetPlayOutScoreboardObjective, Int::class.java)
                typeField = Reflection.getField(packetPlayOutScoreboardObjective, enumScoreboardHealthDisplay)
                typeFieldEnums = enumScoreboardHealthDisplay.enumConstants
                displayNameField = Reflection.getField(packetPlayOutScoreboardObjective, String::class.java, 1)
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }
    }

    enum class Mode(val id: Int) {
        CREATE(0), REMOVE(1), UPDATE(2);
    }

    enum class Type(val id: Int) {
        INTEGER(0), HEARTS(1);
    }

    init {
        nameField.set(handle, name)
        modeField.set(handle, mode.id)
        if (type != null) typeField.set(handle, typeFieldEnums[type.id])
        if (displayName != null) displayNameField.set(handle, displayName)
    }
}
