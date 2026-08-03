package com.beloucif.lataverne.core

/**
 * Le Tribunal ("Le Pilori") - embedded game mode, no content pack (mirrors
 * la-taverne/src/content/tribunal.ts). Charges are store-safe by construction:
 * only abstract penalties, never a named alcohol.
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
)
