package com.beloucif.meskova.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beloucif.meskova.R
import com.beloucif.meskova.core.GROUP_PENALTY
import com.beloucif.meskova.core.JUDGE_PENALTY
import com.beloucif.meskova.core.Player
import com.beloucif.meskova.core.RANKING_QUESTIONS
import com.beloucif.meskova.core.RankingPhase
import com.beloucif.meskova.core.RankingSessionState
import com.beloucif.meskova.core.confirmRanking
import com.beloucif.meskova.core.createRankingSession
import com.beloucif.meskova.core.getContestants
import com.beloucif.meskova.core.getJudge
import com.beloucif.meskova.core.guessQuestion
import com.beloucif.meskova.core.nextRound
import com.beloucif.meskova.core.startGuessing
import com.beloucif.meskova.core.startJudging
import com.beloucif.meskova.core.toggleRanked
import com.beloucif.meskova.ui.theme.MeskovaColors

/**
 * Le Tableau d'Honneur - a judge discovers a secret ranking question and ranks the other
 * players against it. The table then sees the podium and must guess the real question among
 * four. Guessed right: the judge takes [JUDGE_PENALTY]. Guessed wrong: every non-judge player
 * takes [GROUP_PENALTY]. The secret question is only ever shown to the judge during the
 * [RankingPhase.JUDGING] phase, never during guessing or reveal - showing it earlier would let
 * the table cheat. There is no shared engine.penaltyCounts recap on mobile: the tally lives only
 * for this session and is shown on an internal [RankingRecap], [Player.penaltiesStandard] is
 * never mutated.
 */
@Composable
fun RankingScreen(players: List<Player>, onQuit: (roundsPlayed: Int) -> Unit) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var state by remember { mutableStateOf(createRankingSession(RANKING_QUESTIONS, players)) }

    if (state.phase == RankingPhase.FINISHED) {
        RankingRecap(
            state = state,
            onReplay = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                state = createRankingSession(RANKING_QUESTIONS, players)
            },
            onBackToHub = { onQuit(state.roundNumber) },
        )
        return
    }

    val judge = getJudge(state)
    val contestants = getContestants(state)
    val playerName = { id: String -> state.players.firstOrNull { it.id == id }?.name.orEmpty() }
    val groupGuessedRight = state.guessedId == state.round?.question?.id

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskovaColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            IconButton(
                onClick = { onQuit(state.roundNumber) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .background(MeskovaColors.Surface, CircleShape)
                    .border(2.dp, MeskovaColors.Ink, CircleShape)
                    .semantics { contentDescription = context.getString(R.string.ranking_quit_description) },
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = MeskovaColors.Ink)
            }
        }

        Text(
            text = stringResource(R.string.ranking_title, state.roundNumber),
            style = MaterialTheme.typography.labelSmall,
            color = MeskovaColors.InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
        )

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            when (state.phase) {
                RankingPhase.HANDOFF -> judge?.let { RankingHandoff(judgeName = it.name) }

                RankingPhase.JUDGING -> state.round?.let { round ->
                    RankingJudging(
                        secretQuestion = round.question.text,
                        contestants = contestants,
                        ranked = state.ranked,
                        onToggle = { playerId ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = toggleRanked(state, playerId)
                        },
                    )
                }

                RankingPhase.RETURN -> judge?.let { RankingReturn(judgeName = it.name) }

                RankingPhase.GUESSING, RankingPhase.REVEAL -> state.round?.let { round ->
                    RankingGuessing(
                        judgeName = judge?.name.orEmpty(),
                        ranked = state.ranked,
                        playerName = playerName,
                        phase = state.phase,
                        choices = round.choices.map { it.id to it.text },
                        realId = round.question.id,
                        guessedId = state.guessedId,
                        onGuess = { choiceId ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = guessQuestion(state, choiceId)
                        },
                    )
                    if (state.phase == RankingPhase.REVEAL) {
                        Text(
                            text = if (groupGuessedRight) {
                                stringResource(R.string.ranking_reveal_correct, judge?.name.orEmpty(), JUDGE_PENALTY)
                            } else {
                                stringResource(R.string.ranking_reveal_wrong, round.question.text, GROUP_PENALTY)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MeskovaColors.InkSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        )
                    }
                }

                RankingPhase.FINISHED -> Unit
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (state.phase) {
                RankingPhase.HANDOFF -> RankingPrimaryButton(
                    text = stringResource(R.string.ranking_handoff_button, judge?.name.orEmpty()),
                    onClick = { state = startJudging(state) },
                )

                RankingPhase.JUDGING -> RankingPrimaryButton(
                    text = stringResource(R.string.ranking_confirm_button),
                    enabled = state.ranked.size == contestants.size,
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        state = confirmRanking(state)
                    },
                )

                RankingPhase.RETURN -> RankingPrimaryButton(
                    text = stringResource(R.string.ranking_return_button),
                    onClick = { state = startGuessing(state) },
                )

                RankingPhase.REVEAL -> RankingPrimaryButton(
                    text = stringResource(R.string.ranking_next_round),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        state = nextRound(state)
                    },
                )

                RankingPhase.GUESSING, RankingPhase.FINISHED -> Unit
            }
        }
    }
}

@Composable
private fun RankingHandoff(judgeName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MeskovaColors.NeonSoft, RoundedCornerShape(24.dp))
            .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(24.dp))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.ranking_handoff_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MeskovaColors.Ink,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.ranking_handoff_pass, judgeName),
            style = MaterialTheme.typography.headlineMedium,
            color = MeskovaColors.Ink,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.ranking_handoff_body, judgeName),
            style = MaterialTheme.typography.bodySmall,
            color = MeskovaColors.InkSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun RankingJudging(
    secretQuestion: String,
    contestants: List<Player>,
    ranked: List<String>,
    onToggle: (String) -> Unit,
) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MeskovaColors.CardFace, RoundedCornerShape(20.dp))
                .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(20.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.ranking_judging_secret_label),
                style = MaterialTheme.typography.labelSmall,
                color = MeskovaColors.InkMuted,
            )
            Text(
                text = secretQuestion,
                style = MaterialTheme.typography.titleMedium,
                color = MeskovaColors.CardInk,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Text(
            text = stringResource(R.string.ranking_judging_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MeskovaColors.InkSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            contestants.forEach { player ->
                val position = ranked.indexOf(player.id)
                val picked = position != -1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(if (picked) MeskovaColors.NeonSoft else MeskovaColors.Surface, RoundedCornerShape(14.dp))
                        .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(14.dp))
                        .clickable { onToggle(player.id) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(if (picked) MeskovaColors.Ink else MeskovaColors.Surface, CircleShape)
                            .border(2.dp, MeskovaColors.Ink, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (picked) (position + 1).toString() else "-",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (picked) MeskovaColors.Bg else MeskovaColors.InkMuted,
                        )
                    }
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MeskovaColors.Ink,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingReturn(judgeName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MeskovaColors.Neon, RoundedCornerShape(24.dp))
            .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(24.dp))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.ranking_return_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MeskovaColors.CardFace,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.ranking_return_body, judgeName),
            style = MaterialTheme.typography.bodyMedium,
            color = MeskovaColors.CardFace,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun RankingGuessing(
    judgeName: String,
    ranked: List<String>,
    playerName: (String) -> String,
    phase: RankingPhase,
    choices: List<Pair<String, String>>,
    realId: String,
    guessedId: String?,
    onGuess: (String) -> Unit,
) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MeskovaColors.Surface, RoundedCornerShape(16.dp))
                .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.ranking_podium_label, judgeName),
                style = MaterialTheme.typography.labelSmall,
                color = MeskovaColors.InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            ranked.forEachIndexed { index, id ->
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MeskovaColors.InkMuted,
                    )
                    Text(
                        text = playerName(id),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MeskovaColors.Ink,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.ranking_guessing_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MeskovaColors.InkSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            choices.forEach { (choiceId, choiceText) ->
                val isPicked = guessedId == choiceId
                val isReal = realId == choiceId
                val background = when {
                    phase == RankingPhase.GUESSING -> MeskovaColors.Surface
                    phase == RankingPhase.REVEAL && isReal -> MeskovaColors.Success
                    phase == RankingPhase.REVEAL && isPicked && !isReal -> MeskovaColors.CardRed
                    else -> MeskovaColors.Surface
                }
                val textColor = if (phase == RankingPhase.REVEAL && (isReal || isPicked)) {
                    MeskovaColors.CardFace
                } else {
                    MeskovaColors.Ink
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(background, RoundedCornerShape(14.dp))
                        .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(14.dp))
                        .then(
                            if (phase == RankingPhase.GUESSING) {
                                Modifier.clickable { onGuess(choiceId) }
                            } else {
                                Modifier
                            },
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = choiceText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    if (phase == RankingPhase.REVEAL && isReal) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = MeskovaColors.CardFace)
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingPrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MeskovaColors.Neon,
            contentColor = MeskovaColors.CardFace,
            disabledContainerColor = MeskovaColors.NeonSoft,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(16.dp)),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

/**
 * Internal recap for Le Tableau d'Honneur: no shared [com.beloucif.meskova.core.Player.penaltiesStandard]
 * is touched, this only ever reads [RankingSessionState.penaltyCounts] for the lifetime of the session.
 */
@Composable
private fun RankingRecap(state: RankingSessionState, onReplay: () -> Unit, onBackToHub: () -> Unit) {
    val ranked = state.players
        .filter { (state.penaltyCounts[it.id] ?: 0) > 0 }
        .sortedByDescending { state.penaltyCounts[it.id] ?: 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskovaColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.ranking_recap_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MeskovaColors.Neon,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        if (ranked.isEmpty()) {
            Text(
                text = stringResource(R.string.ranking_recap_none),
                style = MaterialTheme.typography.bodyMedium,
                color = MeskovaColors.InkSecondary,
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                items(ranked, key = { it.id }) { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MeskovaColors.Surface, RoundedCornerShape(16.dp))
                            .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MeskovaColors.Ink,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.recap_total_penalties, state.penaltyCounts[player.id] ?: 0),
                            style = MaterialTheme.typography.labelSmall,
                            color = MeskovaColors.Premium,
                        )
                    }
                }
            }
        }

        Button(
            onClick = onReplay,
            colors = ButtonDefaults.buttonColors(containerColor = MeskovaColors.Neon, contentColor = MeskovaColors.CardFace),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text(stringResource(R.string.recap_replay))
        }
        OutlinedButton(
            onClick = onBackToHub,
            border = BorderStroke(2.dp, MeskovaColors.Ink),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text(stringResource(R.string.recap_back_to_hub), color = MeskovaColors.Ink)
        }
    }
}
