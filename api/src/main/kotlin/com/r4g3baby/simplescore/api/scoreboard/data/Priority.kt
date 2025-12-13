package com.r4g3baby.simplescore.api.scoreboard.data

/**
 * Represents the priority levels that can be assigned to actions or elements within the scoreboard system.
 * These priorities help determine the order or importance of operations when multiple actions are requested
 * concurrently. The available priority levels are: Lowest, Low, Normal, High, Highest.
 */
public enum class Priority {
    Lowest, Low, Normal, High, Highest;
}
