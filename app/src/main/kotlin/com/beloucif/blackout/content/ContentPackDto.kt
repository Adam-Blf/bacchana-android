package com.beloucif.blackout.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * kotlinx.serialization mirror of blackout-content/schema/content.schema.json.
 * Lives in :app (not :core) because :core stays a pure-JVM module with zero
 * serialization/Android dependency. [toCore] maps every DTO onto its
 * com.beloucif.blackout.core equivalent once decoded from assets/packs (*.json files).
 */

@Serializable
enum class GameModeDto {
    @SerialName("borderland") BORDERLAND,
    @SerialName("picolo") PICOLO,
    @SerialName("truthOrDare") TRUTH_OR_DARE,
    @SerialName("neverHaveIEver") NEVER_HAVE_I_EVER,
    @SerialName("whoAmong") WHO_AMONG,
    @SerialName("wouldYouRather") WOULD_YOU_RATHER,
    @SerialName("itsA10But") ITS_A_10_BUT,
    @SerialName("sevenSeconds") SEVEN_SECONDS,
    @SerialName("tribunal") TRIBUNAL,
    @SerialName("roulette") ROULETTE,
}

@Serializable
enum class IntensityDto {
    @SerialName("soft") SOFT,
    @SerialName("medium") MEDIUM,
    @SerialName("hot") HOT,
    @SerialName("chaos") CHAOS,
}

@Serializable
enum class RuleTypeDto {
    @SerialName("instant") INSTANT,
    @SerialName("persistent") PERSISTENT,
    @SerialName("role") ROLE,
}

@Serializable
enum class TargetsDto {
    @SerialName("self") SELF,
    @SerialName("chosen") CHOSEN,
    @SerialName("all") ALL,
    @SerialName("gender-m") GENDER_M,
    @SerialName("gender-f") GENDER_F,
    @SerialName("pair") PAIR,
}

@Serializable
data class PenaltyDto(val sips: Int? = null, val shots: Int? = null)

@Serializable
data class RuleDto(val type: RuleTypeDto, val durationTurns: Int? = null)

@Serializable
data class PackItemDto(
    val id: String,
    val text: String,
    val textAlt: String? = null,
    val penalty: PenaltyDto? = null,
    val rule: RuleDto? = null,
    val targets: TargetsDto? = null,
    val tags: List<String> = emptyList(),
    val minPlayers: Int? = null,
)

@Serializable
data class PackMetaDto(
    val id: String,
    val mode: GameModeDto,
    val lang: String,
    val title: String,
    val subtitle: String? = null,
    val premium: Boolean,
    val minPlayers: Int? = null,
    val intensity: IntensityDto? = null,
)

@Serializable
data class ContentPackDto(
    val schemaVersion: Int,
    val pack: PackMetaDto,
    val items: List<PackItemDto>,
)
