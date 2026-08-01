package com.beloucif.blackout.ui

/** Navigation routes. Kept as plain constants - the graph is small enough to not need a sealed hierarchy. */
object BlackOutRoutes {
    const val WELCOME = "welcome"
    const val HUB = "hub"
    const val BORDERLAND = "borderland"
    const val PROMPT = "prompt/{mode}"
    const val RECAP = "recap"

    fun prompt(mode: String) = "prompt/$mode"
}
