package com.beloucif.bacchana.core

/**
 * La Roulette ("La Roue du Destin") - embedded game mode, no content pack (mirrors
 * la-taverne/src/content/roulette.ts).
 * 40 segments, store-safe by construction: abstract penalties, ambiance dares, mimes,
 * votes and soft challenges. Never a named alcohol, never a dangerous instruction.
 */
data class RouletteSegment(
    val id: String,
    val label: String,
    val detail: String,
)

val ROULETTE_SEGMENTS: List<RouletteSegment> = listOf(
    RouletteSegment("rou-01", "1 pénalité", "Le sort est clément, une seule et on n'en parle plus."),
    RouletteSegment("rou-02", "2 pénalités", "La roue hausse le ton, tu en prends deux."),
    RouletteSegment("rou-03", "3 pénalités", "Trois d'un coup, la note grimpe au comptoir."),
    RouletteSegment("rou-04", "Pénalité majeure", "Le pire tirage de la roue, encaisse sans broncher."),
    RouletteSegment("rou-05", "Distribue 2", "Désigne deux convives, chacun prend une pénalité."),
    RouletteSegment("rou-06", "Immunité", "Blindé au prochain tour, plus rien ne t'atteint."),
    RouletteSegment("rou-07", "Toute la table", "Personne n'est épargné, une pénalité pour chacun."),
    RouletteSegment("rou-08", "Rejoue", "Relance la roue immédiatement, le sort n'a pas fini."),
    RouletteSegment("rou-09", "Mime muet", "Mime un métier, la tablée devine en trente secondes."),
    RouletteSegment("rou-10", "Grand accent", "Raconte ta soirée avec l'accent que la table t'impose."),
    RouletteSegment("rou-11", "Statue", "Reste figé comme une statue jusqu'à ton prochain passage."),
    RouletteSegment("rou-12", "Vote express", "La table vote : qui a le plus de culot ce soir ?"),
    RouletteSegment("rou-13", "Duel de regards", "Fixe ton voisin, le premier qui rit prend une pénalité."),
    RouletteSegment("rou-14", "Compliment", "Fais un vrai compliment à la personne à ta gauche."),
    RouletteSegment("rou-15", "Anecdote", "Balance une anecdote gênante en moins d'une minute."),
    RouletteSegment("rou-16", "Imitation", "Imite un convive, il doit deviner de qui il s'agit."),
    RouletteSegment("rou-17", "Refrain", "Chante le refrain que la table te souffle."),
    RouletteSegment("rou-18", "Grimace", "Tiens la grimace la plus laide pendant dix secondes."),
    RouletteSegment("rou-19", "Silence d'or", "Interdit de parler jusqu'à ton prochain passage."),
    RouletteSegment("rou-20", "Slogan", "Invente un slogan pour la soirée, la table juge."),
    RouletteSegment("rou-21", "Capitaine", "Tu mènes le prochain tour, tes règles font loi."),
    RouletteSegment("rou-22", "Chaises musicales", "Change de siège avec la personne assise en face."),
    RouletteSegment("rou-23", "Main faible", "Fais tout de la main gauche jusqu'au prochain tour."),
    RouletteSegment("rou-24", "Confession", "Réponds franchement à une question de la table."),
    RouletteSegment("rou-25", "Vérité ou double", "Réponds vrai ou prends deux pénalités à la place."),
    RouletteSegment("rou-26", "Ovation", "Lève-toi et salue, la table t'offre une ovation."),
    RouletteSegment("rou-27", "Roulement", "Tape la table en rythme, tout le monde doit suivre."),
    RouletteSegment("rou-28", "Pause fraîcheur", "Rapporte un verre d'eau à la personne de ton choix."),
    RouletteSegment("rou-29", "Question piège", "Pose une colle à la table, le premier bloqué prend une pénalité."),
    RouletteSegment("rou-30", "Photo souvenir", "Prends la pose, la table improvise une photo de groupe."),
    RouletteSegment("rou-31", "Deux pas", "Improvise deux pas de danse au choix de la table."),
    RouletteSegment("rou-32", "Accent voyage", "Parle avec un accent étranger jusqu'au prochain tour."),
    RouletteSegment("rou-33", "Éloge", "Fais l'éloge exagéré de ton voisin de droite."),
    RouletteSegment("rou-34", "Pénalité partagée", "Toi et ton voisin prenez une pénalité ensemble."),
    RouletteSegment("rou-35", "Renversement", "La personne à ta droite prend ta pénalité à ta place."),
    RouletteSegment("rou-36", "Défi minute", "La table te lance un défi soft à réaliser sur-le-champ."),
    RouletteSegment("rou-37", "Chef de chœur", "Fais chanter la table trois secondes, à toi de lancer."),
    RouletteSegment("rou-38", "Roue clémente", "Rien ne se passe, savoure ta chance et passe la main."),
    RouletteSegment("rou-39", "Meneur du soir", "Choisis le thème du prochain tour, la table te suit."),
    RouletteSegment("rou-40", "Double ou rien", "Relance : gros lot de pénalités ou immunité, la roue tranche."),
)
