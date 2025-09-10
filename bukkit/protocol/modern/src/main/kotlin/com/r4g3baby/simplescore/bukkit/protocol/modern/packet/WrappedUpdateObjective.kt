package com.r4g3baby.simplescore.bukkit.protocol.modern.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.WrappedChatComponent
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.numbers.WrappedNumberFormat
import com.r4g3baby.simplescore.bukkit.protocol.util.ServerVersion
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection
import org.objenesis.instantiator.ObjectInstantiator
import java.lang.reflect.Modifier
import java.util.Optional

data class WrappedUpdateObjective(
    val name: String,
    val mode: Mode,
    val type: Type? = null,
    val displayName: WrappedChatComponent? = null,
    val numberFormat: WrappedNumberFormat? = null
) : WrappedPacket(packetInstantiator.newInstance()) {
    private companion object {
        private val packetInstantiator: ObjectInstantiator<out Any>

        private val nameField: Reflection.FieldAccessor
        private val modeField: Reflection.FieldAccessor
        private val typeField: Reflection.FieldAccessor
        private val typeFieldEnums: Array<out Any>
        private val displayNameField: Reflection.FieldAccessor
        private val numberFormatField: Reflection.FieldAccessor?

        private val trailsAndTailsUpdate6 = ServerVersion.trailsAndTailsUpdate.copy(build = 6)
        private val afterTrailsAndTailsUpdate6 = ServerVersion.atOrAbove(trailsAndTailsUpdate6)

        init {
            try {
                val packetPlayOutScoreboardObjective = Reflection.findClass(
                    "net.minecraft.network.protocol.game.ClientboundSetObjectivePacket",
                    "net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective",
                    "${Utils.NMS}.PacketPlayOutScoreboardObjective"
                )
                packetInstantiator = Utils.getInstantiatorOf(packetPlayOutScoreboardObjective)

                val enumScoreboardHealthDisplay = Reflection.findClass(
                    "net.minecraft.world.scores.criteria.IScoreboardCriteria\$EnumScoreboardHealthDisplay",
                    "${Utils.NMS}.IScoreboardCriteria\$EnumScoreboardHealthDisplay"
                )

                nameField = Reflection.getField(packetPlayOutScoreboardObjective, String::class.java)
                modeField = Reflection.getField(packetPlayOutScoreboardObjective, Int::class.java) { field ->
                    !Modifier.isStatic(field.modifiers)
                }
                typeField = Reflection.getField(packetPlayOutScoreboardObjective, enumScoreboardHealthDisplay)
                typeFieldEnums = enumScoreboardHealthDisplay.enumConstants
                displayNameField = Reflection.getField(packetPlayOutScoreboardObjective, WrappedChatComponent.clazz)

                numberFormatField = if (afterTrailsAndTailsUpdate6) {
                    Reflection.getField(packetPlayOutScoreboardObjective, Optional::class.java)
                } else if (ServerVersion.atOrAbove(ServerVersion("1.20.3"))) {
                    Reflection.getField(packetPlayOutScoreboardObjective, WrappedNumberFormat.clazz)
                } else null
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
        if (displayName != null) displayNameField.set(handle, displayName.handle)

        if (afterTrailsAndTailsUpdate6) {
            numberFormatField?.set(handle, Optional.ofNullable(numberFormat?.handle))
        } else if (numberFormat != null) {
            numberFormatField?.set(handle, numberFormat.handle)
        }
    }
}
