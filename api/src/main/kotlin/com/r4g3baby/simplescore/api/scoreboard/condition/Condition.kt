package com.r4g3baby.simplescore.api.scoreboard.condition

import com.r4g3baby.simplescore.api.scoreboard.VarReplacer

/**
 * Represents a condition that can be evaluated within a given context.
 *
 * @param V The type of the platform-specific player object.
 */
public interface Condition<V : Any> {
    /**
     * The name of the condition, which serves as an identifier for reference and distinction purposes.
     * It provides a way to uniquely recognize and handle specific conditions when working with a collection of conditions.
     */
    public val name: String

    /**
     * Evaluates the condition in a given context identified by the viewer.
     *
     * @param viewer The context or viewer in which the condition is evaluated.
     * @param varReplacer An instance of VarReplacer used to handle variable replacements within the condition.
     * @return Returns true if the condition is satisfied within the given context, false otherwise.
     */
    public fun check(viewer: V, varReplacer: VarReplacer<V>): Boolean
}
