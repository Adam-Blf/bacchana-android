package com.beloucif.meskova.content

import com.beloucif.meskova.core.ContentPack
import com.beloucif.meskova.core.GameMode
import com.beloucif.meskova.core.Intensity
import com.beloucif.meskova.core.PackItem
import com.beloucif.meskova.core.PackMeta
import com.beloucif.meskova.core.Penalty
import com.beloucif.meskova.core.Rule
import com.beloucif.meskova.core.RuleType
import com.beloucif.meskova.core.Targets

fun GameModeDto.toCore(): GameMode = when (this) {
    GameModeDto.BORDERLAND -> GameMode.BORDERLAND
    GameModeDto.PICOLO -> GameMode.PICOLO
    GameModeDto.TRUTH_OR_DARE -> GameMode.TRUTH_OR_DARE
    GameModeDto.NEVER_HAVE_I_EVER -> GameMode.NEVER_HAVE_I_EVER
    GameModeDto.WHO_AMONG -> GameMode.WHO_AMONG
    GameModeDto.WOULD_YOU_RATHER -> GameMode.WOULD_YOU_RATHER
    GameModeDto.ITS_A_10_BUT -> GameMode.ITS_A_10_BUT
    GameModeDto.SEVEN_SECONDS -> GameMode.SEVEN_SECONDS
    GameModeDto.TRIBUNAL -> GameMode.TRIBUNAL
    GameModeDto.ROULETTE -> GameMode.ROULETTE
}

fun IntensityDto.toCore(): Intensity = when (this) {
    IntensityDto.SOFT -> Intensity.SOFT
    IntensityDto.MEDIUM -> Intensity.MEDIUM
    IntensityDto.HOT -> Intensity.HOT
    IntensityDto.CHAOS -> Intensity.CHAOS
}

fun RuleTypeDto.toCore(): RuleType = when (this) {
    RuleTypeDto.INSTANT -> RuleType.INSTANT
    RuleTypeDto.PERSISTENT -> RuleType.PERSISTENT
    RuleTypeDto.ROLE -> RuleType.ROLE
}

fun TargetsDto.toCore(): Targets = when (this) {
    TargetsDto.SELF -> Targets.SELF
    TargetsDto.CHOSEN -> Targets.CHOSEN
    TargetsDto.ALL -> Targets.ALL
    TargetsDto.GENDER_M -> Targets.GENDER_M
    TargetsDto.GENDER_F -> Targets.GENDER_F
    TargetsDto.PAIR -> Targets.PAIR
    TargetsDto.SINGLE -> Targets.SINGLE
    TargetsDto.COUPLE -> Targets.COUPLE
}

fun PenaltyDto.toCore(): Penalty = Penalty(sips, shots)

fun RuleDto.toCore(): Rule = Rule(type.toCore(), durationTurns)

fun PackItemDto.toCore(): PackItem = PackItem(
    id = id,
    text = text,
    textAlt = textAlt,
    penalty = penalty?.toCore(),
    rule = rule?.toCore(),
    targets = targets?.toCore(),
    tags = tags,
    minPlayers = minPlayers,
)

fun PackMetaDto.toCore(): PackMeta = PackMeta(
    id = id,
    mode = mode.toCore(),
    lang = lang,
    title = title,
    subtitle = subtitle,
    premium = premium,
    minPlayers = minPlayers,
    intensity = intensity?.toCore(),
)

fun ContentPackDto.toCore(): ContentPack = ContentPack(
    schemaVersion = schemaVersion,
    pack = pack.toCore(),
    items = items.map { it.toCore() },
)
