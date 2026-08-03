package com.beloucif.lataverne.ui

/** Navigation routes. Kept as plain constants - the graph is small enough to not need a sealed hierarchy. */
object LaTaverneRoutes {
    const val WELCOME = "welcome"
    const val HUB = "hub"
    const val BORDERLAND = "borderland"
    const val PROMPT = "prompt/{mode}"
    const val RECAP = "recap"
    const val ROULETTE = "roulette"
    const val TRIBUNAL = "tribunal"
    const val AUCTION = "auction"
    const val QUIZ = "quiz"
    const val RANKING = "ranking"
    const val WOULD_YOU_RATHER = "would-you-rather"
    const val PAYWALL = "paywall"

    fun prompt(mode: String) = "prompt/$mode"
}
