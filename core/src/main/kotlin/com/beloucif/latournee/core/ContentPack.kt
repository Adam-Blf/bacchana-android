package com.beloucif.latournee.core

/**
 * Kotlin mirror of the JSON schema in la-taverne-content/schema/content.schema.json.
 * Kept dependency-free (no kotlinx.serialization) so :core stays pure JVM; the :app
 * module maps its @Serializable DTOs onto these types after decoding assets.
 */

/** Every mode the multi-mode engine knows about. Mirrors GameMode from engine/types.ts. */
enum class GameMode {
    BORDERLAND,
    PICOLO,
    TRUTH_OR_DARE,
    NEVER_HAVE_I_EVER,
    WHO_AMONG,
    WOULD_YOU_RATHER,
    ITS_A_10_BUT,
    SEVEN_SECONDS,
    TRIBUNAL,
    ROULETTE,
    AUCTION,
    QUIZ,
    RANKING,
}

enum class Intensity { SOFT, MEDIUM, HOT, CHAOS }

enum class RuleType { INSTANT, PERSISTENT, ROLE }

enum class Targets { SELF, CHOSEN, ALL, GENDER_M, GENDER_F, PAIR, SINGLE, COUPLE }

data class Penalty(val sips: Int? = null, val shots: Int? = null)

data class Rule(val type: RuleType, val durationTurns: Int? = null)

data class PackItem(
    val id: String,
    val text: String,
    val textAlt: String? = null,
    val penalty: Penalty? = null,
    val rule: Rule? = null,
    val targets: Targets? = null,
    val tags: List<String> = emptyList(),
    val minPlayers: Int? = null,
)

data class PackMeta(
    val id: String,
    val mode: GameMode,
    val lang: String,
    val title: String,
    val subtitle: String? = null,
    val premium: Boolean,
    val minPlayers: Int? = null,
    val intensity: Intensity? = null,
)

data class ContentPack(
    val schemaVersion: Int,
    val pack: PackMeta,
    val items: List<PackItem>,
)
