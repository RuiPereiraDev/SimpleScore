package com.r4g3baby.simplescore.bukkit.protocol.legacy.packet

import com.r4g3baby.simplescore.bukkit.protocol.WrappedPacket
import com.r4g3baby.simplescore.bukkit.protocol.util.Utils
import com.r4g3baby.simplescore.core.util.Reflection

data class WrappedUpdateTeam(
    val teamName: String,
    val mode: TeamMode,
    val displayName: String? = null,
    val prefix: String? = null,
    val suffix: String? = null,
    val players: Collection<String>? = null
) : WrappedPacket(packetPlayOutScoreboardTeam.newInstance()) {
    private companion object {
        private val packetPlayOutScoreboardTeam: Class<*>

        private val teamNameField: Reflection.FieldAccessor
        private val modeField: Reflection.FieldAccessor
        private val displayNameField: Reflection.FieldAccessor
        private val prefixField: Reflection.FieldAccessor
        private val suffixField: Reflection.FieldAccessor
        private val playersField: Reflection.FieldAccessor

        init {
            try {
                packetPlayOutScoreboardTeam = Reflection.getClass(
                    "${Utils.NMS}.PacketPlayOutScoreboardTeam"
                )

                teamNameField = Reflection.getField(packetPlayOutScoreboardTeam, String::class.java, 0)
                modeField = Reflection.getField(packetPlayOutScoreboardTeam, Int::class.java, 1)
                displayNameField = Reflection.getField(packetPlayOutScoreboardTeam, String::class.java, 1)
                prefixField = Reflection.getField(packetPlayOutScoreboardTeam, String::class.java, 2)
                suffixField = Reflection.getField(packetPlayOutScoreboardTeam, String::class.java, 3)
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
        if (displayName != null) displayNameField.set(handle, displayName)
        if (prefix != null) prefixField.set(handle, prefix)
        if (suffix != null) suffixField.set(handle, suffix)
        if (players != null) playersField.set(handle, players)
    }
}