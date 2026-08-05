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
import com.beloucif.meskova.core.MINORITY_PENALTY
import com.beloucif.meskova.core.Player
import com.beloucif.meskova.core.VoteSide
import com.beloucif.meskova.core.WOULD_YOU_RATHER_QUESTIONS
import com.beloucif.meskova.core.WouldYouRatherPhase
import com.beloucif.meskova.core.WouldYouRatherSessionState
import com.beloucif.meskova.core.allVoted
import com.beloucif.meskova.core.castVote
import com.beloucif.meskova.core.createWouldYouRatherSession
import com.beloucif.meskova.core.getMinoritySide
import com.beloucif.meskova.core.nextRound
import com.beloucif.meskova.core.revealVotes
import com.beloucif.meskova.ui.theme.MeskovaColors

/**
 * Tu preferes - a dilemma A or B is shown to the whole table, the phone stays put and every
 * active player taps their own camp in turn. On reveal, the minority camp takes
 * [MINORITY_PENALTY] each - a perfect tie or a unanimous vote costs nobody anything. No shared
 * engine.penaltyCounts recap on mobile: the tally lives only for this session and is shown on an
 * internal [WouldYouRatherRecap], [Player.penaltiesStandard] is never mutated.
 */
@Composable
fun WouldYouRatherScreen(players: List<Player>, onQuit: (roundsPlayed: Int) -> Unit) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var state by remember { mutableStateOf(createWouldYouRatherSession(WOULD_YOU_RATHER_QUESTIONS, players)) }

    if (state.phase == WouldYouRatherPhase.FINISHED) {
        WouldYouRatherRecap(
            state = state,
            onReplay = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                state = createWouldYouRatherSession(WOULD_YOU_RATHER_QUESTIONS, players)
            },
            onBackToHub = { onQuit(state.roundNumber) },
        )
        return
    }

    val currentVoter = state.players.firstOrNull { it.id !in state.votes.keys }
    val total = state.roundNumber + state.queue.size
    val minoritySide = getMinoritySide(state.votes)

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
                    .semantics { contentDescription = context.getString(R.string.wyr_quit_description) },
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = MeskovaColors.Ink)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.wyr_title),
                style = MaterialTheme.typography.labelSmall,
                color = MeskovaColors.InkMuted,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.wyr_round_progress, state.roundNumber, total),
                style = MaterialTheme.typography.labelSmall,
                color = MeskovaColors.InkMuted,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            state.currentQuestion?.let { question ->
                WouldYouRatherDilemma(
                    optionA = question.optionA,
                    optionB = question.optionB,
                    phase = state.phase,
                    minoritySide = minoritySide,
                    votes = state.votes,
                    currentVoterId = currentVoter?.id,
                    onVote = { side ->
                        if (currentVoter != null) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = castVote(state, currentVoter.id, side)
                        }
                    },
                )
            }

            when (state.phase) {
                WouldYouRatherPhase.VOTING -> currentVoter?.let { voter ->
                    Text(
                        text = stringResource(R.string.wyr_current_voter, voter.name),
                        style = MaterialTheme.typography.titleMedium,
                        color = MeskovaColors.Ink,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    )
                } ?: Text(
                    text = stringResource(R.string.wyr_votes_progress, state.votes.size, state.players.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MeskovaColors.InkMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                )

                WouldYouRatherPhase.REVEAL -> {
                    val minorityNames = state.votes.entries
                        .filter { it.value == minoritySide }
                        .mapNotNull { entry -> state.players.firstOrNull { it.id == entry.key }?.name }
                    Text(
                        text = when {
                            minoritySide == null -> stringResource(R.string.wyr_outcome_no_penalty)
                            else -> stringResource(
                                R.string.wyr_outcome_minority,
                                minorityNames.joinToString(", "),
                                MINORITY_PENALTY,
                            )
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MeskovaColors.InkSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    )
                }

                WouldYouRatherPhase.FINISHED -> Unit
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (state.phase) {
                WouldYouRatherPhase.VOTING -> WouldYouRatherPrimaryButton(
                    text = stringResource(R.string.wyr_reveal_button),
                    enabled = allVoted(state),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        state = revealVotes(state)
                    },
                )

                WouldYouRatherPhase.REVEAL -> WouldYouRatherPrimaryButton(
                    text = stringResource(R.string.wyr_next_round),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        state = nextRound(state)
                    },
                )

                WouldYouRatherPhase.FINISHED -> Unit
            }
        }
    }
}

@Composable
private fun WouldYouRatherDilemma(
    optionA: String,
    optionB: String,
    phase: WouldYouRatherPhase,
    minoritySide: VoteSide?,
    votes: Map<String, VoteSide>,
    currentVoterId: String?,
    onVote: (VoteSide) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        WouldYouRatherOptionCard(
            label = stringResource(R.string.wyr_option_a_label),
            text = optionA,
            side = VoteSide.A,
            phase = phase,
            minoritySide = minoritySide,
            voteCount = votes.values.count { it == VoteSide.A },
            enabled = currentVoterId != null,
            onClick = { onVote(VoteSide.A) },
        )
        WouldYouRatherOptionCard(
            label = stringResource(R.string.wyr_option_b_label),
            text = optionB,
            side = VoteSide.B,
            phase = phase,
            minoritySide = minoritySide,
            voteCount = votes.values.count { it == VoteSide.B },
            enabled = currentVoterId != null,
            onClick = { onVote(VoteSide.B) },
        )
    }
}

@Composable
private fun WouldYouRatherOptionCard(
    label: String,
    text: String,
    side: VoteSide,
    phase: WouldYouRatherPhase,
    minoritySide: VoteSide?,
    voteCount: Int,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val background = when {
        phase == WouldYouRatherPhase.REVEAL && minoritySide == side -> MeskovaColors.CardRed
        else -> MeskovaColors.CardFace
    }
    val textColor = if (phase == WouldYouRatherPhase.REVEAL && minoritySide == side) {
        MeskovaColors.CardFace
    } else {
        MeskovaColors.CardInk
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(24.dp))
            .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(24.dp))
            .then(if (phase == WouldYouRatherPhase.VOTING && enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(20.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                // CardAccent, not Neon: this label sits on the fixed white CardFace surface in
                // both themes, but Neon is recalculated per theme against the themed Bg - its
                // dark-theme value (a bright, light orange) drops to 2.60:1 on white, below AA.
                // CardAccent is the fixed dark-orange equivalent for a surface that never
                // changes, same logic as CardInk vs Ink.
                color = if (phase == WouldYouRatherPhase.REVEAL && minoritySide == side) textColor else MeskovaColors.CardAccent,
                fontWeight = FontWeight.Bold,
            )
            if (phase == WouldYouRatherPhase.REVEAL) {
                Text(
                    text = voteCount.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun WouldYouRatherPrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        // TileInk, not CardFace: Neon/NeonSoft stay light in both themes, white text on them
        // drops to 3.28:1 (light) / 2.60:1 (dark), below the 4.5:1 AA floor. See
        // MeskovaColors.TileInk KDoc.
        colors = ButtonDefaults.buttonColors(
            containerColor = MeskovaColors.Neon,
            contentColor = MeskovaColors.TileInk,
            disabledContainerColor = MeskovaColors.NeonSoft,
            disabledContentColor = MeskovaColors.TileInk,
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
 * Internal recap for Tu preferes: no shared [com.beloucif.meskova.core.Player.penaltiesStandard]
 * is touched, this only ever reads [WouldYouRatherSessionState.penaltyCounts] for the lifetime of
 * this session.
 */
@Composable
private fun WouldYouRatherRecap(state: WouldYouRatherSessionState, onReplay: () -> Unit, onBackToHub: () -> Unit) {
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
            text = stringResource(R.string.wyr_recap_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MeskovaColors.Neon,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        if (ranked.isEmpty()) {
            Text(
                text = stringResource(R.string.wyr_recap_none),
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
            // TileInk, not CardFace: see MeskovaColors.TileInk KDoc.
            colors = ButtonDefaults.buttonColors(containerColor = MeskovaColors.Neon, contentColor = MeskovaColors.TileInk),
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
