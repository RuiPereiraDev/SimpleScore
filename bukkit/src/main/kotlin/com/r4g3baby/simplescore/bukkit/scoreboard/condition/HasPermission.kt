package com.r4g3baby.simplescore.bukkit.scoreboard.condition

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer
import com.r4g3baby.simplescore.core.scoreboard.condition.Condition
import org.bukkit.entity.Player

data class HasPermission(
    override val name: String,
    override val negate: Boolean,
    val permission: String, val parsePermission: Boolean
) : Condition<Player>() {
    override fun pass(viewer: Player, varReplacer: VarReplacer<Player>): Boolean {
        val perm = if (parsePermission) varReplacer.replace(permission, viewer) else permission
        return viewer.hasPermission(perm)
    }
}