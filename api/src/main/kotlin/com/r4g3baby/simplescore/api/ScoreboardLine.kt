package com.r4g3baby.simplescore.api

public interface ScoreboardLine<V : Any> : Conditional<V> {
    public companion object {
        public const val DEFAULT_UPDATE_TICKS: Int = 20
        public const val DEFAULT_RENDER_TICKS: Int = 10
    }

    public val textEffects: List<TextEffect>

    public fun tick()

    public fun shouldRender(): Boolean

    public fun currentText(viewer: V, varReplacer: VarReplacer<V>): String

    public fun ScoreboardLine<V>.applEffects(text: String): String {
        var finalText = text
        textEffects.forEach { textEffect ->
            finalText = textEffect.apply(finalText)
        }
        return finalText
    }
}