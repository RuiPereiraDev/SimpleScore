package com.r4g3baby.simplescore.api.scoreboard.condition

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer

/**
 * Represents an object that possesses a set of conditions which determine its visibility.
 *
 * @param V The type of the platform-specific player object.
 */
public interface Conditional<V : Any> {
    /**
     * A list of conditions that determine visibility within a specific context. Each condition is evaluated based on the
     * platform-specific player object type defined by the generic parameter V. The conditions collectively decide whether
     * a particular object can be seen by a viewer.
     */
    public val conditions: List<Condition<V>>

    /**
     * Determines whether an object can be seen by a specific viewer based on a set of conditions.
     *
     * @param viewer The viewer in which visibility is evaluated. It represents the platform-specific player object.
     * @param varReplacer An instance of [VarReplacer] used to handle variable replacements within the condition checks.
     * @return Returns true if all conditions are satisfied (the object is visible to the viewer), false otherwise.
     */
    public fun canSee(viewer: V, varReplacer: VarReplacer<V>): Boolean {
        return conditions.all { it.check(viewer, varReplacer) }
    }
}
