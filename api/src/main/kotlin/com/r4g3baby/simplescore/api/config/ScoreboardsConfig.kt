package com.r4g3baby.simplescore.api.config

import com.r4g3baby.simplescore.api.scoreboard.Scoreboard

public interface ScoreboardsConfig<V : Any> {
    public val scoreboards: Map<String, Scoreboard<V>>
}