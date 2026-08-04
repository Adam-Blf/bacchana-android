package com.beloucif.meskova.core

/**
 * Le Tribunal ("Le Pilori") - embedded game mode, no content pack (mirrors
 * la-taverne/src/content/tribunal.ts). Charges are store-safe by construction:
 * never a named alcohol, only abstract grievances of the evening.
 */
data class TribunalCharge(
    val id: String,
    val text: String,
)

val TRIBUNAL_CHARGES: List<TribunalCharge> = listOf(
    TribunalCharge(
        "tri-01",
        "Complot de silence : avoir laissé un pote se faire accuser sans lever le petit doigt.",
    ),
    TribunalCharge(
        "tri-02",
        "Usage abusif du téléphone en pleine partie, au vu et au su de la table.",
    ),
    TribunalCharge(
        "tri-03",
        "Trahison caractérisée : avoir voté contre son propre binôme au tour précédent.",
    ),
    TribunalCharge(
        "tri-04",
        "Rire suspect en pleine question sérieuse, laissant planer le doute.",
    ),
    TribunalCharge(
        "tri-05",
        "Fuite d'information : avoir soufflé une réponse à voix (trop) basse.",
    ),
    TribunalCharge(
        "tri-06",
        "Triche présumée sur un pierre-feuille-ciseaux disputé.",
    ),
    TribunalCharge(
        "tri-07",
        "Abandon de poste : s'être levé sans prévenir la cour.",
    ),
    TribunalCharge(
        "tri-08",
        "Mensonge flagrant sur son propre score de la soirée.",
    ),
    TribunalCharge(
        "tri-09",
        "Complicité de retard général en ayant proposé une pause non votée.",
    ),
    TribunalCharge(
        "tri-10",
        "Détournement d'attention : avoir changé de sujet pour éviter une pénalité.",
    ),
    TribunalCharge(
        "tri-11",
        "Avoir juré \"juste une dernière manche\" au moins cinq fois dans la soirée.",
    ),
    TribunalCharge(
        "tri-12",
        "Monopolisation de la parole : n'avoir laissé personne finir une phrase.",
    ),
    TribunalCharge(
        "tri-13",
        "Avoir ri de sa propre blague avant même de l'avoir racontée.",
    ),
    TribunalCharge(
        "tri-14",
        "Sabotage d'ambiance : avoir baissé la musique sans consulter la cour.",
    ),
    TribunalCharge(
        "tri-15",
        "Avoir prétendu connaître les règles pour mieux les inventer au fil du jeu.",
    ),
    TribunalCharge(
        "tri-16",
        "Favoritisme flagrant envers un joueur pour éviter de se faire accuser.",
    ),
    TribunalCharge(
        "tri-17",
        "Avoir consulté ses messages en plein vote décisif.",
    ),
    TribunalCharge(
        "tri-18",
        "Trahison gastronomique : avoir fini le dernier morceau sans le proposer.",
    ),
    TribunalCharge(
        "tri-19",
        "Avoir contesté chaque décision de la cour depuis le début de la partie.",
    ),
    TribunalCharge(
        "tri-20",
        "Retard à l'allumage : avoir mis trois tours à comprendre la consigne.",
    ),
    TribunalCharge(
        "tri-21",
        "Avoir soudoyé un juré avec un compliment clairement intéressé.",
    ),
    TribunalCharge(
        "tri-22",
        "Diffamation de comptoir : avoir inventé un ragot sur un absent.",
    ),
    TribunalCharge(
        "tri-23",
        "Avoir feint l'innocence avec un sourire qui trahit tout.",
    ),
    TribunalCharge(
        "tri-24",
        "Chantage amical : avoir menacé de tout révéler pour éviter une pénalité.",
    ),
    TribunalCharge(
        "tri-25",
        "Avoir changé les règles en cours de route à son seul avantage.",
    ),
    TribunalCharge(
        "tri-26",
        "Absentéisme : s'être éclipsé au moment crucial du vote.",
    ),
    TribunalCharge(
        "tri-27",
        "Avoir juré ses grands dieux ne pas avoir triché, preuve du contraire à la clé.",
    ),
    TribunalCharge(
        "tri-28",
        "Provocation en règle : avoir défié la cour de le condamner.",
    ),
    TribunalCharge(
        "tri-29",
        "Avoir feint de ne pas entendre la question pour gagner du temps.",
    ),
    TribunalCharge(
        "tri-30",
        "Népotisme : avoir systématiquement voté pour épargner son meilleur pote.",
    ),
    TribunalCharge(
        "tri-31",
        "Avoir tenté de retourner la table contre l'accusé précédent pour se blanchir.",
    ),
    TribunalCharge(
        "tri-32",
        "Faux témoignage : avoir juré avoir vu ce que personne d'autre n'a vu.",
    ),
    TribunalCharge(
        "tri-33",
        "Avoir gardé le meilleur siège toute la soirée sans jamais le céder.",
    ),
    TribunalCharge(
        "tri-34",
        "Manipulation d'ambiance : avoir relancé un sujet gênant exprès.",
    ),
    TribunalCharge(
        "tri-35",
        "Avoir sous-estimé la cour en pensant s'en tirer avec un clin d'œil.",
    ),
    TribunalCharge(
        "tri-36",
        "Récidive : deuxième passage au pilori dans la même soirée.",
    ),
    TribunalCharge(
        "tri-37",
        "Avoir promis un service qu'il n'avait aucune intention de rendre.",
    ),
    TribunalCharge(
        "tri-38",
        "Complicité passive : avoir vu la triche et n'avoir rien dit.",
    ),
    TribunalCharge(
        "tri-39",
        "Avoir monopolisé le rôle de meneur sans jamais le partager.",
    ),
    TribunalCharge(
        "tri-40",
        "Outrage à la cour : avoir levé les yeux au ciel pendant le réquisitoire.",
    ),
)
