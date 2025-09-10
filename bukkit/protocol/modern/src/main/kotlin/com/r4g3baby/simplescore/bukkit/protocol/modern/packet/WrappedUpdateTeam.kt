package com.r4g3baby.simplescore.bukkit.protocol.modern.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.modern.chat.WrappedChatComponent
import com.r4g3baby.simplescore.bukkit.protocol.util.ServerVersion
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection
import org.objenesis.instantiator.ObjectInstantiator
import java.lang.reflect.Modifier
import java.util.*

data class WrappedUpdateTeam(
    val teamName: String,
    val mode: TeamMode,
    val displayName: WrappedChatComponent? = null,
    val prefix: WrappedChatComponent? = null,
    val suffix: WrappedChatComponent? = null,
    val players: Collection<String>? = null
) : WrappedPacket(packetInstantiator.newInstance()) {
    private companion object {
        private val packetInstantiator: ObjectInstantiator<out Any>

        private val teamNameField: Reflection.FieldAccessor
        private val modeField: Reflection.FieldAccessor

        private val teamParametersField: Reflection.FieldAccessor?
        private val displayNameField: Reflection.FieldAccessor?
        private val prefixField: Reflection.FieldAccessor?
        private val suffixField: Reflection.FieldAccessor?
        private val friendlyFireField: Reflection.FieldAccessor?
        private val nameTagVisibilityField: Reflection.FieldAccessor?
        private val collisionRuleField: Reflection.FieldAccessor?
        private val chatFormatField: Reflection.FieldAccessor?
        private val chatFormatFieldEnums: Array<out Any>?

        private val playersField: Reflection.FieldAccessor

        init {
            try {
                val packetPlayOutScoreboardTeam = Reflection.findClass(
                    "net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket",
                    "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam",
                    "${Utils.NMS}.PacketPlayOutScoreboardTeam"
                )
                packetInstantiator = Utils.getInstantiatorOf(packetPlayOutScoreboardTeam)

                teamNameField = Reflection.getField(packetPlayOutScoreboardTeam, String::class.java, 0)
                modeField = Reflection.getField(packetPlayOutScoreboardTeam, Int::class.java, 0) {
                    !Modifier.isStatic(it.modifiers)
                }

                if (ServerVersion.atOrAbove(ServerVersion.cavesAndCliffsPartIUpdate)) {
                    teamParametersField = Reflection.getField(packetPlayOutScoreboardTeam, Optional::class.java)
                    displayNameField = null
                    prefixField = null
                    suffixField = null
                    friendlyFireField = null
                    nameTagVisibilityField = null
                    collisionRuleField = null
                    chatFormatField = null
                    chatFormatFieldEnums = null
                } else {
                    val enumChatFormat = Reflection.getClass("${Utils.NMS}.EnumChatFormat")

                    teamParametersField = null
                    displayNameField = Reflection.getField(packetPlayOutScoreboardTeam, WrappedChatComponent.clazz, 0)
                    prefixField = Reflection.getField(packetPlayOutScoreboardTeam, WrappedChatComponent.clazz, 1)
                    suffixField = Reflection.getField(packetPlayOutScoreboardTeam, WrappedChatComponent.clazz, 2)
                    friendlyFireField = Reflection.getField(packetPlayOutScoreboardTeam, Int::class.java, 1)
                    nameTagVisibilityField = Reflection.getField(packetPlayOutScoreboardTeam, String::class.java, 1)
                    collisionRuleField = Reflection.getField(packetPlayOutScoreboardTeam, String::class.java, 2)
                    chatFormatField = Reflection.getField(packetPlayOutScoreboardTeam, enumChatFormat)
                    chatFormatFieldEnums = enumChatFormat.enumConstants
                }

                playersField = Reflection.getField(packetPlayOutScoreboardTeam, Collection::class.java)
            } catch (throwable: Throwable) {
                throw ExceptionInInitializerError(throwable)
            }
        }
    }

    enum class TeamMode(val id: Int) {
        CREATE(0), REMOVE(1), UPDATE(2), ADD_PLAYERS(3), REMOVE_PLAYERS(4);
    }

    init {
        teamNameField.set(handle, teamName)
        modeField.set(handle, mode.id)

        if (teamParametersField != null) {
            val teamParameters = if (displayName != null && prefix != null && suffix != null) {
                WrappedTeamParameters(displayName, prefix, suffix)
            } else null
            teamParametersField.set(handle, Optional.ofNullable(teamParameters?.handle))
        } else {
            if (displayName != null) displayNameField?.set(handle, displayName.handle)
            if (prefix != null) prefixField?.set(handle, prefix.handle)
            if (suffix != null) suffixField?.set(handle, suffix.handle)

            friendlyFireField?.set(handle, 0)
            nameTagVisibilityField?.set(handle, "always")
            collisionRuleField?.set(handle, "always")
            chatFormatField?.set(handle, chatFormatFieldEnums?.get(21))
        }

        if (players != null) playersField.set(handle, players)
    }

    private data class WrappedTeamParameters(
        val displayName: WrappedChatComponent,
        val prefix: WrappedChatComponent,
        val suffix: WrappedChatComponent,
    ) {
        private companion object {
            private val classInstantiator: ObjectInstantiator<out Any>

            private val displayNameField: Reflection.FieldAccessor
            private val prefixField: Reflection.FieldAccessor
            private val suffixField: Reflection.FieldAccessor

            private val nameTagVisibilityField: Reflection.FieldAccessor
            private val collisionRuleField: Reflection.FieldAccessor
            private val chatFormatField: Reflection.FieldAccessor
            private val chatFormatFieldEnums: Array<out Any>
            private val friendlyFireField: Reflection.FieldAccessor

            init {
                try {
                    val teamInfoClass = Reflection.findClass(
                        "net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket",
                        "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam"
                    ).declaredClasses[0]
                    classInstantiator = Utils.getInstantiatorOf(teamInfoClass)

                    displayNameField = Reflection.getField(teamInfoClass, WrappedChatComponent.clazz, 0)
                    prefixField = Reflection.getField(teamInfoClass, WrappedChatComponent.clazz, 1)
                    suffixField = Reflection.getField(teamInfoClass, WrappedChatComponent.clazz, 2)

                    val enumChatFormat = Reflection.findClass("net.minecraft.EnumChatFormat")

                    nameTagVisibilityField = Reflection.getField(teamInfoClass, String::class.java, 0)
                    collisionRuleField = Reflection.getField(teamInfoClass, String::class.java, 1)
                    chatFormatField = Reflection.getField(teamInfoClass, enumChatFormat)
                    chatFormatFieldEnums = enumChatFormat.enumConstants
                    friendlyFireField = Reflection.getField(teamInfoClass, Int::class.java, 0)
                } catch (throwable: Throwable) {
                    throw ExceptionInInitializerError(throwable)
                }
            }
        }

        val handle: Any = classInstantiator.newInstance()

        init {
            displayNameField.set(handle, displayName.handle)
            prefixField.set(handle, prefix.handle)
            suffixField.set(handle, suffix.handle)

            nameTagVisibilityField.set(handle, "always")
            collisionRuleField.set(handle, "always")
            chatFormatField.set(handle, chatFormatFieldEnums[21])
            friendlyFireField.set(handle, 0)
        }
    }
}
