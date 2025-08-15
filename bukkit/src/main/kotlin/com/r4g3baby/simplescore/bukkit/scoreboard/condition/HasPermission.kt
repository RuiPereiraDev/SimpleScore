package com.r4g3baby.simplescore.bukkit.scoreboard.condition

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.api.scoreboard.condition.Condition
import org.bukkit.entity.Player

data class HasPermission(
    override val name: String,
    val permission: String, val parsePermission: Boolean
) : Condition<Player> {
    override fun check(viewer: Player, varReplacer: VarReplacer<Player>): Boolean {
        val perm = if (parsePermission) varReplacer.replace(permission, viewer) else permission
        return viewer.hasPermission(perm)
    }
}