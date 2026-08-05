package com.beloucif.latournee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.beloucif.latournee.R
import com.beloucif.latournee.core.Player
import com.beloucif.latournee.core.QUIZ_QUESTIONS
import com.beloucif.latournee.core.QuizCategory
import com.beloucif.latournee.core.QuizOutcome
import com.beloucif.latournee.core.QuizPhase
import com.beloucif.latournee.core.QuizSessionState
import com.beloucif.latournee.core.answerCorrect
import com.beloucif.latournee.core.answerWrong
import com.beloucif.latournee.core.createQuizSession
import com.beloucif.latournee.core.distributePot
import com.beloucif.latournee.core.getCurrentQuizPlayer
import com.beloucif.latournee.core.keepPot
import com.beloucif.latournee.ui.theme.LaTourneeColors

/**
 * Quitte ou Trinque - culture-generale quiz with a cagnotte. Correct answer: the points
 * join the current player's cagnotte, then they choose - keep it (quitte ou double at the
 * next turn) or distribute it (glory to the table, resets to zero). Wrong answer: they take
 * their cagnotte + the question's points as penalties. No shared engine.penaltyCounts recap
 * on mobile: the tally lives only for this session and is shown on an internal [QuizRecap],
 * [Player.penaltiesStandard] is never mutated.
 */
@Composable
fun QuizScreen(players: List<Player>, onQuit: (turnsPlayed: Int) -> Unit) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var state by remember { mutableStateOf(createQuizSession(QUIZ_QUESTIONS, players)) }
    var answerShown by remember { mutableStateOf(false) }

    if (state.phase == QuizPhase.FINISHED) {
        QuizRecap(
            state = state,
            onReplay = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                state = createQuizSession(QUIZ_QUESTIONS, players)
                answerShown = false
            },
            onBackToHub = { onQuit(state.turnNumber) },
        )
        return
    }

    val currentPlayer = getCurrentQuizPlayer(state)
    val pot = currentPlayer?.let { state.pots[it.id] ?: 0 } ?: 0
    val total = state.turnNumber + state.queue.size

    fun handleCorrect() {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        state = answerCorrect(state)
        answerShown = false
    }

    fun handleWrong() {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        state = answerWrong(state)
        answerShown = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LaTourneeColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            IconButton(
                onClick = { onQuit(state.turnNumber) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .background(LaTourneeColors.Surface, CircleShape)
                    .border(2.dp, LaTourneeColors.Ink, CircleShape)
                    .semantics { contentDescription = context.getString(R.string.quiz_quit_description) },
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = LaTourneeColors.Ink)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.quiz_title),
                style = MaterialTheme.typography.labelSmall,
                color = LaTourneeColors.InkMuted,
                textAlign = TextAlign.Center,
            )
            Text(
                text = currentPlayer?.name.orEmpty(),
                style = MaterialTheme.typography.headlineMedium,
                color = LaTourneeColors.Ink,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QuizBadge(text = stringResource(R.string.quiz_cagnotte, pot), background = LaTourneeColors.NeonSoft, contentColor = LaTourneeColors.TileInk)
                Text(
                    text = stringResource(R.string.quiz_progress, state.turnNumber, total),
                    style = MaterialTheme.typography.labelSmall,
                    color = LaTourneeColors.InkMuted,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            when (state.phase) {
                QuizPhase.QUESTION -> state.currentQuestion?.let { question ->
                    QuizQuestionCard(
                        categoryLabel = categoryDisplayName(question.category),
                        pointsLabel = stringResource(R.string.quiz_points_at_stake, state.currentPoints),
                        questionText = question.question,
                        answerText = question.answer,
                        answerShown = answerShown,
                        onReveal = { answerShown = true },
                    )
                }

                QuizPhase.CHOICE -> QuizChoiceCard(pot = pot)

                QuizPhase.FINISHED -> Unit
            }

            state.lastOutcome?.let { outcome ->
                if (state.phase == QuizPhase.QUESTION) {
                    val name = state.players.firstOrNull { it.id == outcome.playerId }?.name.orEmpty()
                    val text = when (outcome) {
                        is QuizOutcome.Busted -> stringResource(R.string.quiz_outcome_busted, name, outcome.amount)
                        is QuizOutcome.Banked -> stringResource(R.string.quiz_outcome_banked, name, outcome.amount)
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall,
                        color = LaTourneeColors.InkSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (state.phase) {
                QuizPhase.QUESTION -> Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedButton(
                        onClick = ::handleWrong,
                        modifier = Modifier.weight(1f).height(56.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, LaTourneeColors.Ink),
                    ) {
                        Text(stringResource(R.string.quiz_wrong), color = LaTourneeColors.Ink)
                    }
                    QuizPrimaryButton(
                        text = stringResource(R.string.quiz_correct),
                        modifier = Modifier.weight(1f),
                        onClick = ::handleCorrect,
                    )
                }

                QuizPhase.CHOICE -> {
                    QuizPrimaryButton(
                        text = stringResource(R.string.quiz_distribute, pot),
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = distributePot(state)
                        },
                    )
                    OutlinedButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = keepPot(state)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, LaTourneeColors.Ink),
                    ) {
                        Text(stringResource(R.string.quiz_keep), color = LaTourneeColors.Ink)
                    }
                }

                QuizPhase.FINISHED -> Unit
            }
        }
    }
}

/**
 * [contentColor] defaults to [LaTourneeColors.Ink] (correct for the `Surface` background used by
 * the category badge) but must be passed explicitly as [LaTourneeColors.TileInk] wherever the
 * background is `NeonSoft` (cagnotte/points badges) - that fill stays light in both themes, so
 * the themed `Ink` would invert to near-white in dark theme. See LaTourneeColors.TileInk KDoc.
 */
@Composable
private fun QuizBadge(
    text: String,
    background: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color = LaTourneeColors.Ink,
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(50))
            .border(2.dp, LaTourneeColors.Ink, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun QuizQuestionCard(
    categoryLabel: String,
    pointsLabel: String,
    questionText: String,
    answerText: String,
    answerShown: Boolean,
    onReveal: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LaTourneeColors.CardFace, RoundedCornerShape(24.dp))
            .border(2.dp, LaTourneeColors.Ink, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuizBadge(text = categoryLabel, background = LaTourneeColors.Surface)
            QuizBadge(text = pointsLabel, background = LaTourneeColors.NeonSoft, contentColor = LaTourneeColors.TileInk)
        }
        Text(
            text = questionText,
            style = MaterialTheme.typography.titleMedium,
            color = LaTourneeColors.CardInk,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        if (answerShown) {
            Text(
                text = answerText,
                style = MaterialTheme.typography.bodyMedium,
                color = LaTourneeColors.CardInk,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .background(LaTourneeColors.NeonSoft, RoundedCornerShape(16.dp))
                    .border(2.dp, LaTourneeColors.Ink, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        } else {
            OutlinedButton(
                onClick = onReveal,
                modifier = Modifier.padding(top = 16.dp).height(44.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, LaTourneeColors.Ink),
            ) {
                Text(stringResource(R.string.quiz_reveal_answer), color = LaTourneeColors.Ink)
            }
        }
    }
}

@Composable
private fun QuizChoiceCard(pot: Int) {
    // TileInk everywhere on this card, not Ink/InkSecondary: it sits on a NeonSoft fill, which
    // stays light in both themes. See LaTourneeColors.TileInk KDoc.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LaTourneeColors.NeonSoft, RoundedCornerShape(24.dp))
            .border(2.dp, LaTourneeColors.Ink, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.quiz_choice_title),
            style = MaterialTheme.typography.headlineSmall,
            color = LaTourneeColors.TileInk,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.quiz_choice_body, pot),
            style = MaterialTheme.typography.bodyMedium,
            color = LaTourneeColors.TileInk,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun QuizPrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        // TileInk, not CardFace: see LaTourneeColors.TileInk KDoc.
        colors = ButtonDefaults.buttonColors(
            containerColor = LaTourneeColors.Neon,
            contentColor = LaTourneeColors.TileInk,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(2.dp, LaTourneeColors.Ink, RoundedCornerShape(16.dp)),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

private fun categoryDisplayName(category: QuizCategory): String = when (category) {
    QuizCategory.CULTURE_G -> "Culture G"
    QuizCategory.SPORT -> "Sport"
    QuizCategory.MUSIQUE -> "Musique"
    QuizCategory.CINE_SERIES -> "Ciné & séries"
    QuizCategory.A_TABLE -> "À table"
    QuizCategory.HISTOIRE_GEO -> "Histoire-géo"
}

/**
 * Internal recap for Quitte ou Trinque: no shared [com.beloucif.latournee.core.Player.penaltiesStandard]
 * is touched, this only ever reads [QuizSessionState.penaltyCounts] for the lifetime of the session.
 * [QuizSessionState.distributedCounts] is glory, never shown as a penalty here.
 */
@Composable
private fun QuizRecap(state: QuizSessionState, onReplay: () -> Unit, onBackToHub: () -> Unit) {
    val ranked = state.players
        .filter { (state.penaltyCounts[it.id] ?: 0) > 0 }
        .sortedByDescending { state.penaltyCounts[it.id] ?: 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LaTourneeColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.quiz_recap_title),
            style = MaterialTheme.typography.headlineMedium,
            color = LaTourneeColors.Neon,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        if (ranked.isEmpty()) {
            Text(
                text = stringResource(R.string.quiz_recap_none),
                style = MaterialTheme.typography.bodyMedium,
                color = LaTourneeColors.InkSecondary,
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
                            .background(LaTourneeColors.Surface, RoundedCornerShape(16.dp))
                            .border(2.dp, LaTourneeColors.Ink, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = LaTourneeColors.Ink,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.recap_total_penalties, state.penaltyCounts[player.id] ?: 0),
                            style = MaterialTheme.typography.labelSmall,
                            color = LaTourneeColors.Premium,
                        )
                    }
                }
            }
        }

        Button(
            onClick = onReplay,
            // TileInk, not CardFace: see LaTourneeColors.TileInk KDoc.
            colors = ButtonDefaults.buttonColors(containerColor = LaTourneeColors.Neon, contentColor = LaTourneeColors.TileInk),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text(stringResource(R.string.recap_replay))
        }
        OutlinedButton(
            onClick = onBackToHub,
            border = androidx.compose.foundation.BorderStroke(2.dp, LaTourneeColors.Ink),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text(stringResource(R.string.recap_back_to_hub), color = LaTourneeColors.Ink)
        }
    }
}
