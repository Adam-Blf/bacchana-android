package com.beloucif.bacchus.core

/**
 * Quitte ou Trinque - embedded mode, no content pack (mirrors
 * la-taverne/src/content/quiz.ts). Culture-generale questions, checked orally
 * by the table. Store-safe by construction: never a named alcohol.
 */
enum class QuizCategory {
    CULTURE_G,
    SPORT,
    MUSIQUE,
    CINE_SERIES,
    A_TABLE,
    HISTOIRE_GEO,
}

data class QuizQuestion(
    val id: String,
    val category: QuizCategory,
    val question: String,
    val answer: String,
)

val QUIZ_QUESTIONS: List<QuizQuestion> = listOf(
    // Histoire-géo
    QuizQuestion("qz-001", QuizCategory.HISTOIRE_GEO, "Quelle est la capitale de l'Australie ?", "Canberra (et non Sydney)"),
    QuizQuestion("qz-002", QuizCategory.HISTOIRE_GEO, "En quelle année est tombé le mur de Berlin ?", "1989"),
    QuizQuestion("qz-003", QuizCategory.HISTOIRE_GEO, "Quel est le plus long fleuve de France ?", "La Loire"),
    QuizQuestion(
        "qz-004",
        QuizCategory.HISTOIRE_GEO,
        "Combien y a-t-il de pays frontaliers de la France métropolitaine ?",
        "8 (Belgique, Luxembourg, Allemagne, Suisse, Italie, Monaco, Espagne, Andorre)",
    ),
    QuizQuestion("qz-005", QuizCategory.HISTOIRE_GEO, "Quel océan borde la Californie ?", "L'océan Pacifique"),
    QuizQuestion(
        "qz-006",
        QuizCategory.HISTOIRE_GEO,
        "Qui était le premier président de la Ve République française ?",
        "Charles de Gaulle",
    ),
    QuizQuestion("qz-007", QuizCategory.HISTOIRE_GEO, "Quelle est la plus haute montagne d'Afrique ?", "Le Kilimandjaro"),
    QuizQuestion("qz-008", QuizCategory.HISTOIRE_GEO, "Dans quel pays se trouve le Machu Picchu ?", "Au Pérou"),
    QuizQuestion("qz-009", QuizCategory.HISTOIRE_GEO, "Quelle mer sépare l'Europe de l'Afrique ?", "La mer Méditerranée"),
    QuizQuestion("qz-010", QuizCategory.HISTOIRE_GEO, "En quelle année a eu lieu le premier pas sur la Lune ?", "1969"),

    // Culture G
    QuizQuestion("qz-011", QuizCategory.CULTURE_G, "Combien de côtés possède un hexagone ?", "6"),
    QuizQuestion("qz-012", QuizCategory.CULTURE_G, "Quel est le symbole chimique de l'or ?", "Au"),
    QuizQuestion("qz-013", QuizCategory.CULTURE_G, "Combien de dents possède un adulte (dents de sagesse comprises) ?", "32"),
    QuizQuestion("qz-014", QuizCategory.CULTURE_G, "Quelle planète est la plus proche du Soleil ?", "Mercure"),
    QuizQuestion("qz-015", QuizCategory.CULTURE_G, "Qui a peint « La Nuit étoilée » ?", "Vincent van Gogh"),
    QuizQuestion(
        "qz-016",
        QuizCategory.CULTURE_G,
        "Combien de temps met la Terre pour faire le tour du Soleil ?",
        "Environ 365 jours (un an)",
    ),
    QuizQuestion(
        "qz-017",
        QuizCategory.CULTURE_G,
        "Quel animal est le plus rapide du monde en pointe ?",
        "Le faucon pèlerin (en piqué) - accepté : le guépard sur terre",
    ),
    QuizQuestion("qz-018", QuizCategory.CULTURE_G, "Combien y a-t-il de lettres dans l'alphabet français ?", "26"),
    QuizQuestion("qz-019", QuizCategory.CULTURE_G, "Quel écrivain a créé le personnage d'Arsène Lupin ?", "Maurice Leblanc"),
    QuizQuestion("qz-020", QuizCategory.CULTURE_G, "De quelle couleur est le sang d'une pieuvre ?", "Bleu"),

    // Sport
    QuizQuestion("qz-021", QuizCategory.SPORT, "Combien de joueurs composent une équipe de football sur le terrain ?", "11"),
    QuizQuestion("qz-022", QuizCategory.SPORT, "Tous les combien d'années ont lieu les Jeux olympiques d'été ?", "Tous les 4 ans"),
    QuizQuestion(
        "qz-023",
        QuizCategory.SPORT,
        "Dans quel sport parle-t-on de « grand chelem » ?",
        "Le tennis (accepté : rugby, baseball)",
    ),
    QuizQuestion("qz-024", QuizCategory.SPORT, "Combien de points vaut un essai au rugby ?", "5 points"),
    QuizQuestion("qz-025", QuizCategory.SPORT, "Quelle est la distance officielle d'un marathon ?", "42,195 km"),
    QuizQuestion("qz-026", QuizCategory.SPORT, "Quel pays a remporté la Coupe du monde de football 2018 ?", "La France"),
    QuizQuestion("qz-027", QuizCategory.SPORT, "Dans quel sport s'illustre Teddy Riner ?", "Le judo"),
    QuizQuestion(
        "qz-028",
        QuizCategory.SPORT,
        "Combien de sets faut-il gagner pour remporter un match de tennis en Grand Chelem chez les hommes ?",
        "3 sets",
    ),
    QuizQuestion(
        "qz-029",
        QuizCategory.SPORT,
        "Quelle course cycliste se termine traditionnellement sur les Champs-Élysées ?",
        "Le Tour de France",
    ),
    QuizQuestion("qz-030", QuizCategory.SPORT, "Au basket, combien de points vaut un panier marqué derrière la ligne ?", "3 points"),

    // Musique
    QuizQuestion("qz-031", QuizCategory.MUSIQUE, "Combien de cordes possède une guitare classique ?", "6"),
    QuizQuestion("qz-032", QuizCategory.MUSIQUE, "Quel groupe anglais a chanté « Bohemian Rhapsody » ?", "Queen"),
    QuizQuestion("qz-033", QuizCategory.MUSIQUE, "Quel chanteur belge a popularisé « Ne me quitte pas » ?", "Jacques Brel"),
    QuizQuestion("qz-034", QuizCategory.MUSIQUE, "Combien de touches possède un piano standard ?", "88"),
    QuizQuestion("qz-035", QuizCategory.MUSIQUE, "Quelle chanteuse française est surnommée « la Môme » ?", "Édith Piaf"),
    QuizQuestion("qz-036", QuizCategory.MUSIQUE, "Quel instrument joue principalement un batteur ?", "La batterie"),
    QuizQuestion("qz-037", QuizCategory.MUSIQUE, "De quel pays vient le groupe ABBA ?", "La Suède"),
    QuizQuestion("qz-038", QuizCategory.MUSIQUE, "Quel rappeur français a sorti l'album « Deux frères » ?", "PNL"),
    QuizQuestion(
        "qz-039",
        QuizCategory.MUSIQUE,
        "Combien y a-t-il de notes dans une gamme majeure classique (sans l'octave) ?",
        "7",
    ),
    QuizQuestion(
        "qz-040",
        QuizCategory.MUSIQUE,
        "Quel roi de la pop a inventé le moonwalk (ou l'a rendu célèbre) ?",
        "Michael Jackson",
    ),

    // Ciné & séries
    QuizQuestion("qz-041", QuizCategory.CINE_SERIES, "Quel réalisateur a signé « Pulp Fiction » ?", "Quentin Tarantino"),
    QuizQuestion("qz-042", QuizCategory.CINE_SERIES, "Comment s'appelle l'école de sorciers dans Harry Potter ?", "Poudlard"),
    QuizQuestion("qz-043", QuizCategory.CINE_SERIES, "Quel acteur incarne Jack dans « Titanic » ?", "Leonardo DiCaprio"),
    QuizQuestion("qz-044", QuizCategory.CINE_SERIES, "Dans quelle ville se déroule la série « Friends » ?", "New York"),
    QuizQuestion(
        "qz-045",
        QuizCategory.CINE_SERIES,
        "Quel est le prénom du héros de « Retour vers le futur » ?",
        "Marty (McFly)",
    ),
    QuizQuestion("qz-046", QuizCategory.CINE_SERIES, "Quel studio d'animation a créé « Toy Story » ?", "Pixar"),
    QuizQuestion(
        "qz-047",
        QuizCategory.CINE_SERIES,
        "Comment s'appelle le vaisseau de Han Solo dans Star Wars ?",
        "Le Faucon Millenium",
    ),
    QuizQuestion(
        "qz-048",
        QuizCategory.CINE_SERIES,
        "Quelle série espagnole met en scène un braquage de la Fabrique de la monnaie ?",
        "La Casa de Papel",
    ),
    QuizQuestion("qz-049", QuizCategory.CINE_SERIES, "Quel personnage vert et grincheux vit dans un marais ?", "Shrek"),
    QuizQuestion(
        "qz-050",
        QuizCategory.CINE_SERIES,
        "Combien y a-t-il de films dans la saga « Le Seigneur des anneaux » (trilogie originale) ?",
        "3",
    ),

    // À table
    QuizQuestion("qz-051", QuizCategory.A_TABLE, "Quel fromage italien est l'ingrédient star du tiramisu ?", "Le mascarpone"),
    QuizQuestion("qz-052", QuizCategory.A_TABLE, "De quel pays vient la paella ?", "L'Espagne"),
    QuizQuestion(
        "qz-053",
        QuizCategory.A_TABLE,
        "Quel légume est la base du guacamole ?",
        "L'avocat (techniquement un fruit - accepté)",
    ),
    QuizQuestion(
        "qz-054",
        QuizCategory.A_TABLE,
        "Quelle pâtisserie française porte le nom d'un éclair de génie ? Indice : chocolat ou café.",
        "L'éclair",
    ),
    QuizQuestion("qz-055", QuizCategory.A_TABLE, "Quel est l'ingrédient principal du houmous ?", "Les pois chiches"),
    QuizQuestion("qz-056", QuizCategory.A_TABLE, "De quelle région française vient la quiche ?", "La Lorraine"),
    QuizQuestion("qz-057", QuizCategory.A_TABLE, "Quel fruit sec est à la base du Nutella (avec le cacao) ?", "La noisette"),
    QuizQuestion(
        "qz-058",
        QuizCategory.A_TABLE,
        "Comment appelle-t-on un plat de pâtes aux œufs, lardons et parmesan ?",
        "Une carbonara",
    ),
    QuizQuestion("qz-059", QuizCategory.A_TABLE, "Quelle épice donne sa couleur jaune au curry ?", "Le curcuma"),
    QuizQuestion("qz-060", QuizCategory.A_TABLE, "Quel pays a inventé les sushis ?", "Le Japon"),
)
