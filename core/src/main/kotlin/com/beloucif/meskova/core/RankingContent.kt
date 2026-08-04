package com.beloucif.meskova.core

/**
 * Le Tableau d'Honneur - embedded mode, no content pack (mirrors
 * la-taverne/src/content/ranking.ts). Secret ranking questions, 100 % original.
 * The judge ranks their friends against one of them, the table has to guess
 * the real question among 4. Store-safe: never a named alcohol.
 */
data class RankingQuestion(
    val id: String,
    val text: String,
)

val RANKING_QUESTIONS: List<RankingQuestion> = listOf(
    RankingQuestion("rk-01", "Du plus susceptible de devenir célèbre au moins susceptible"),
    RankingQuestion("rk-02", "Du plus gros dormeur au plus matinal"),
    RankingQuestion("rk-03", "Du plus dramatique au plus zen"),
    RankingQuestion("rk-04", "Du plus radin au plus dépensier"),
    RankingQuestion("rk-05", "Du plus accro à son téléphone au plus détaché"),
    RankingQuestion("rk-06", "Du plus susceptible de survivre à une apocalypse zombie au premier éliminé"),
    RankingQuestion("rk-07", "Du meilleur danseur au plus grand danger public en soirée"),
    RankingQuestion("rk-08", "Du plus mauvais perdant au plus fair-play"),
    RankingQuestion("rk-09", "Du plus susceptible d'arriver en retard à son propre mariage au plus ponctuel"),
    RankingQuestion("rk-10", "Du plus grand cœur d'artichaut au plus difficile à séduire"),
    RankingQuestion("rk-11", "Du plus têtu au plus influençable"),
    RankingQuestion("rk-12", "Du plus susceptible de finir président au plus anarchiste"),
    RankingQuestion("rk-13", "Du plus gourmand au plus difficile à table"),
    RankingQuestion("rk-14", "Du plus maladroit au plus adroit de ses mains"),
    RankingQuestion("rk-15", "Du plus bavard au plus mystérieux"),
    RankingQuestion("rk-16", "Du plus susceptible de pleurer devant un film au plus insensible"),
    RankingQuestion("rk-17", "Du plus aventurier au plus casanier"),
    RankingQuestion("rk-18", "Du plus fort en mytho au plus transparent"),
    RankingQuestion("rk-19", "Du plus stylé au plus « confort avant tout »"),
    RankingQuestion("rk-20", "Du plus susceptible d'oublier un anniversaire au plus attentionné"),
    RankingQuestion("rk-21", "Du plus compétitif au plus « c'est juste un jeu »"),
    RankingQuestion("rk-22", "Du plus susceptible de se perdre avec un GPS au meilleur sens de l'orientation"),
    RankingQuestion("rk-23", "Du plus fêtard au premier à rentrer"),
    RankingQuestion("rk-24", "Du plus susceptible d'adopter cinq chats au plus allergique aux animaux"),
    RankingQuestion("rk-25", "Du plus beau parleur au plus timide"),
    RankingQuestion("rk-26", "Du plus susceptible de devenir millionnaire au plus fâché avec l'argent"),
    RankingQuestion("rk-27", "Du plus grand chef cuisinier au roi des pâtes trop cuites"),
    RankingQuestion("rk-28", "Du plus susceptible de faire le tour du monde au plus attaché à sa ville"),
    RankingQuestion("rk-29", "Du plus grand enfant au plus vieux dans sa tête"),
    RankingQuestion("rk-30", "Du plus susceptible de chanter en public au plus discret sous la douche"),
    RankingQuestion("rk-31", "Du plus optimiste au plus pessimiste"),
    RankingQuestion("rk-32", "Du plus susceptible de répondre « oui » à tout au roi du « non »"),
    RankingQuestion("rk-33", "Du plus sportif au plus canapé"),
    RankingQuestion("rk-34", "Du plus susceptible d'écrire un livre au plus fâché avec les mots"),
    RankingQuestion("rk-35", "Du plus curieux au plus « chacun sa vie »"),
    RankingQuestion("rk-36", "Du plus susceptible de gagner un jeu télévisé au plus distrait"),
    RankingQuestion("rk-37", "Du plus romantique au plus pragmatique"),
    RankingQuestion("rk-38", "Du plus susceptible de parler aux inconnus au plus réservé"),
    RankingQuestion("rk-39", "Du plus grand procrastinateur au plus organisé"),
    RankingQuestion("rk-40", "Du plus susceptible de tout quitter pour élever des chèvres au plus urbain"),
)
