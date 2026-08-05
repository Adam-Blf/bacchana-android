package com.beloucif.bacchus.ui.screens

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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.beloucif.bacchus.R
import com.beloucif.bacchus.core.Player
import com.beloucif.bacchus.core.TribunalEngine
import com.beloucif.bacchus.core.TribunalPhase
import com.beloucif.bacchus.core.TribunalState
import com.beloucif.bacchus.ui.theme.BacchusColors

/**
 * Le Tribunal ("Le Pilori") - embedded mode, no content pack. Each active player can either
 * write a secret accusation against the table, or let the app deal from [TRIBUNAL_CHARGES].
 * One accusation is drawn per trial, a random accused (never the author) is picked, the table
 * votes guilty/not guilty and a majority-guilty verdict adds a flat +1 penalty. There is no
 * shared engine.penaltyCounts recap on mobile: the tally lives only for this session and is
 * shown on an internal [TribunalRecap], [Player.penaltiesStandard] is never mutated.
 */
@Composable
fun TribunalScreen(players: List<Player>, onQuit: (trialsPlayed: Int) -> Unit) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var state by remember { mutableStateOf(TribunalEngine.start(players)) }
    var draft by remember { mutableStateOf("") }

    if (state.phase == TribunalPhase.FINISHED) {
        TribunalRecap(
            state = state,
            onReplay = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                state = TribunalEngine.replay(state)
                draft = ""
            },
            onBackToHub = { onQuit(state.trialsPlayed) },
        )
        return
    }

    val accused = state.accused
    val chargeText = if (state.phase == TribunalPhase.DEFENSE || state.phase == TribunalPhase.VOTE) {
        TribunalEngine.chargeText(state)
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BacchusColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            IconButton(
                onClick = { onQuit(state.trialsPlayed) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .background(BacchusColors.Surface, CircleShape)
                    .border(2.dp, BacchusColors.Ink, CircleShape)
                    .semantics { contentDescription = context.getString(R.string.tribunal_quit_description) },
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = BacchusColors.Ink)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.tribunal_title),
                style = MaterialTheme.typography.labelSmall,
                color = BacchusColors.InkMuted,
                textAlign = TextAlign.Center,
            )
            if ((state.phase == TribunalPhase.DEFENSE || state.phase == TribunalPhase.VOTE) && accused != null) {
                Text(
                    text = accused.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = BacchusColors.Ink,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.tribunal_appears_before_court),
                    style = MaterialTheme.typography.bodySmall,
                    color = BacchusColors.InkSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            when (state.phase) {
                TribunalPhase.INTRO -> TribunalIntro(
                    onWriteOwn = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        draft = ""
                        state = TribunalEngine.startCollect(state)
                    },
                    onAppCharges = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        state = TribunalEngine.useAppCharges(state)
                    },
                )

                TribunalPhase.COLLECT_HANDOFF -> state.writer?.let { writer ->
                    TribunalHandoff(
                        writerName = writer.name,
                        progress = state.writerIndex + 1,
                        total = state.activePlayers.size,
                    )
                }

                TribunalPhase.COLLECT_WRITE -> state.writer?.let { writer ->
                    TribunalWrite(
                        writerName = writer.name,
                        draft = draft,
                        onDraftChange = { draft = it.take(200) },
                    )
                }

                TribunalPhase.DEFENSE, TribunalPhase.VOTE -> if (chargeText != null && state.current != null) {
                    TribunalCharge(
                        chargeText = chargeText,
                        isAnonymous = state.current?.authorId != null,
                        phase = state.phase,
                        accusedName = accused?.name.orEmpty(),
                        votesGuilty = state.votesGuilty,
                        votesInnocent = state.votesInnocent,
                        verdict = state.verdict,
                        onVoteGuilty = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = TribunalEngine.voteGuilty(state)
                        },
                        onVoteInnocent = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = TribunalEngine.voteInnocent(state)
                        },
                    )
                }

                TribunalPhase.FINISHED -> Unit
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (state.phase) {
                TribunalPhase.COLLECT_HANDOFF -> state.writer?.let { writer ->
                    TribunalPrimaryButton(
                        text = stringResource(R.string.tribunal_confirm_writer, writer.name),
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = TribunalEngine.confirmWriter(state)
                        },
                    )
                }

                TribunalPhase.COLLECT_WRITE -> TribunalPrimaryButton(
                    text = stringResource(R.string.tribunal_write_submit),
                    enabled = draft.isNotBlank(),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        state = TribunalEngine.submitAccusation(state, draft)
                        draft = ""
                    },
                )

                TribunalPhase.DEFENSE -> TribunalPrimaryButton(
                    text = stringResource(R.string.tribunal_pass_to_vote),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        state = TribunalEngine.goToVote(state)
                    },
                )

                TribunalPhase.VOTE -> if (state.verdict == null) {
                    TribunalPrimaryButton(
                        text = stringResource(R.string.tribunal_verdict_button),
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = TribunalEngine.handleVerdict(state)
                        },
                    )
                } else {
                    TribunalPrimaryButton(
                        text = if (state.pool.isNotEmpty()) {
                            stringResource(R.string.tribunal_next_trial)
                        } else {
                            stringResource(R.string.tribunal_new_trial)
                        },
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = TribunalEngine.newTrial(state)
                        },
                    )
                    OutlinedButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            state = TribunalEngine.finish(state)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, BacchusColors.Ink),
                    ) {
                        Text(stringResource(R.string.tribunal_finish), color = BacchusColors.Ink)
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun TribunalIntro(onWriteOwn: () -> Unit, onAppCharges: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.tribunal_intro_title),
            style = MaterialTheme.typography.headlineMedium,
            color = BacchusColors.Ink,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.tribunal_intro_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = BacchusColors.InkSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        TribunalPrimaryButton(text = stringResource(R.string.tribunal_intro_write_own), onClick = onWriteOwn)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = onAppCharges,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, BacchusColors.Ink),
        ) {
            Text(
                text = stringResource(R.string.tribunal_intro_app_charges),
                style = MaterialTheme.typography.titleMedium,
                color = BacchusColors.Ink,
            )
        }
    }
}

@Composable
private fun TribunalHandoff(writerName: String, progress: Int, total: Int) {
    // TileInk, not Ink: this card sits on a NeonSoft fill, which stays light in both themes.
    // See BacchusColors.TileInk KDoc.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BacchusColors.NeonSoft, RoundedCornerShape(24.dp))
            .border(2.dp, BacchusColors.Ink, RoundedCornerShape(24.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.tribunal_handoff_progress, progress, total),
                style = MaterialTheme.typography.labelMedium,
                color = BacchusColors.TileInk,
            )
            Text(
                text = stringResource(R.string.tribunal_handoff_pass, writerName),
                style = MaterialTheme.typography.headlineSmall,
                color = BacchusColors.TileInk,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun TribunalWrite(writerName: String, draft: String, onDraftChange: (String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.tribunal_write_hint, writerName),
            style = MaterialTheme.typography.bodyMedium,
            color = BacchusColors.InkSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        OutlinedTextField(
            value = draft,
            onValueChange = onDraftChange,
            placeholder = { Text(stringResource(R.string.tribunal_write_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 6,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BacchusColors.Neon,
                unfocusedBorderColor = BacchusColors.Ink,
                focusedContainerColor = BacchusColors.Surface,
                unfocusedContainerColor = BacchusColors.Surface,
            ),
        )
        Text(
            text = stringResource(R.string.tribunal_write_counter, draft.length),
            style = MaterialTheme.typography.labelSmall,
            color = BacchusColors.InkMuted,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        )
    }
}

@Composable
private fun TribunalCharge(
    chargeText: String,
    isAnonymous: Boolean,
    phase: TribunalPhase,
    accusedName: String,
    votesGuilty: Int,
    votesInnocent: Int,
    verdict: Boolean?,
    onVoteGuilty: () -> Unit,
    onVoteInnocent: () -> Unit,
) {
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = 4.dp, y = 4.dp)
                    .background(BacchusColors.Ink, RoundedCornerShape(24.dp)),
            ) { Box(modifier = Modifier.padding(40.dp)) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BacchusColors.CardFace, RoundedCornerShape(24.dp))
                    .border(2.dp, BacchusColors.Ink, RoundedCornerShape(24.dp))
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = chargeText,
                    style = MaterialTheme.typography.titleMedium,
                    color = BacchusColors.CardInk,
                    textAlign = TextAlign.Center,
                )
                if (isAnonymous) {
                    Text(
                        text = stringResource(R.string.tribunal_anonymous_note),
                        style = MaterialTheme.typography.labelSmall,
                        color = BacchusColors.InkMuted,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
        }

        if (phase == TribunalPhase.DEFENSE) {
            Text(
                text = stringResource(R.string.tribunal_defense_hint, accusedName),
                style = MaterialTheme.typography.bodyMedium,
                color = BacchusColors.InkSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            )
        }

        if (phase == TribunalPhase.VOTE) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VoteButton(
                    label = stringResource(R.string.tribunal_vote_guilty),
                    count = votesGuilty,
                    enabled = verdict == null,
                    onClick = onVoteGuilty,
                    modifier = Modifier.weight(1f),
                )
                VoteButton(
                    label = stringResource(R.string.tribunal_vote_innocent),
                    count = votesInnocent,
                    enabled = verdict == null,
                    onClick = onVoteInnocent,
                    modifier = Modifier.weight(1f),
                )
            }

            verdict?.let {
                Text(
                    text = if (it) {
                        stringResource(R.string.tribunal_verdict_guilty)
                    } else {
                        stringResource(R.string.tribunal_verdict_innocent)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (it) BacchusColors.Neon else BacchusColors.Success,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                )
            }
        }
    }
}

@Composable
private fun VoteButton(
    label: String,
    count: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(72.dp)
            .background(BacchusColors.Surface, RoundedCornerShape(16.dp))
            .border(2.dp, BacchusColors.Ink, RoundedCornerShape(16.dp))
            .then(if (enabled) Modifier.clickableSafely(onClick) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = BacchusColors.Ink,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = BacchusColors.InkMuted,
        )
    }
}

private fun Modifier.clickableSafely(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)

@Composable
private fun TribunalPrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        // TileInk, not CardFace: Neon/NeonSoft stay light in both themes, white text on them
        // drops to 3.28:1 (light) / 2.60:1 (dark), below the 4.5:1 AA floor. See
        // BacchusColors.TileInk KDoc.
        colors = ButtonDefaults.buttonColors(
            containerColor = BacchusColors.Neon,
            contentColor = BacchusColors.TileInk,
            disabledContainerColor = BacchusColors.NeonSoft,
            disabledContentColor = BacchusColors.TileInk,
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(2.dp, BacchusColors.Ink, RoundedCornerShape(16.dp)),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

/**
 * Internal recap for Le Tribunal: no shared [com.beloucif.bacchus.core.Player.penaltiesStandard]
 * is touched, this only ever reads [TribunalState.penaltyCounts] for the lifetime of the session.
 */
@Composable
private fun TribunalRecap(state: TribunalState, onReplay: () -> Unit, onBackToHub: () -> Unit) {
    val ranked = state.activePlayers
        .filter { (state.penaltyCounts[it.id] ?: 0) > 0 }
        .sortedByDescending { state.penaltyCounts[it.id] ?: 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BacchusColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.tribunal_recap_title),
            style = MaterialTheme.typography.headlineMedium,
            color = BacchusColors.Neon,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        if (ranked.isEmpty()) {
            Text(
                text = stringResource(R.string.tribunal_recap_none),
                style = MaterialTheme.typography.bodyMedium,
                color = BacchusColors.InkSecondary,
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
                            .background(BacchusColors.Surface, RoundedCornerShape(16.dp))
                            .border(2.dp, BacchusColors.Ink, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = BacchusColors.Ink,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.recap_total_penalties, state.penaltyCounts[player.id] ?: 0),
                            style = MaterialTheme.typography.labelSmall,
                            color = BacchusColors.Premium,
                        )
                    }
                }
            }
        }

        Button(
            onClick = onReplay,
            // TileInk, not CardFace: see BacchusColors.TileInk KDoc.
            colors = ButtonDefaults.buttonColors(containerColor = BacchusColors.Neon, contentColor = BacchusColors.TileInk),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text(stringResource(R.string.recap_replay))
        }
        OutlinedButton(
            onClick = onBackToHub,
            border = androidx.compose.foundation.BorderStroke(2.dp, BacchusColors.Ink),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text(stringResource(R.string.recap_back_to_hub), color = BacchusColors.Ink)
        }
    }
}
