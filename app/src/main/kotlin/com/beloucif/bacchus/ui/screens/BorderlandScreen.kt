package com.beloucif.bacchus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beloucif.bacchus.R
import com.beloucif.bacchus.core.BorderlandState
import com.beloucif.bacchus.core.Card
import com.beloucif.bacchus.core.GamePhase
import com.beloucif.bacchus.core.Player
import com.beloucif.bacchus.core.Suit
import com.beloucif.bacchus.ui.BorderlandViewModel
import com.beloucif.bacchus.ui.theme.BacchusColors

/** Le Coupe-Gorge: the giant white card at the center on cream paper, minimal HUD in Space Mono. */
@Composable
fun BorderlandScreen(
    players: List<Player>,
    viewModel: BorderlandViewModel,
    onGameOver: () -> Unit,
    onExit: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(players) {
        if (state.players.isEmpty() && players.size >= 2) viewModel.start(players)
    }

    LaunchedEffect(state.gamePhase) {
        if (state.gamePhase == GamePhase.ENDED) onGameOver()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BacchusColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
    ) {
        BorderlandTopBar(state = state, onExit = onExit)

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            val card = state.currentCard
            if (card == null) {
                Button(onClick = viewModel::drawCard) {
                    Text(stringResource(R.string.borderland_draw_card))
                }
            } else {
                PlayingCard(
                    card = card,
                    revealed = state.isCardRevealed,
                    onClick = { if (!state.isCardRevealed) viewModel.revealCard() },
                )
            }
        }

        state.currentPlayer?.let { current ->
            Text(
                text = stringResource(R.string.borderland_current_player, current.name),
                style = MaterialTheme.typography.titleMedium,
                color = BacchusColors.InkSecondary,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        when {
            state.contestState.active -> ContestControls(
                players = players,
                onEscalate = { viewModel.escalateContest(it) },
                onResolve = { viewModel.resolveContest(); viewModel.nextTurn() },
                onCancel = viewModel::cancelContest,
            )

            state.currentCard != null && state.isCardRevealed -> Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = { players.firstOrNull()?.let { viewModel.startContest(it) } },
                    modifier = Modifier.weight(1f),
                ) { Text(stringResource(R.string.borderland_contest)) }
                Button(onClick = viewModel::nextTurn, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.borderland_next_turn))
                }
            }
        }
    }
}

@Composable
private fun BorderlandTopBar(state: BorderlandState, onExit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.borderland_title),
            style = MaterialTheme.typography.headlineMedium,
            color = BacchusColors.Neon,
            modifier = Modifier.clickable(onClick = onExit),
        )
        Text(
            text = stringResource(R.string.borderland_cards_remaining, state.cardsRemaining),
            style = MaterialTheme.typography.labelSmall,
            color = BacchusColors.InkMuted,
        )
    }
}

@Composable
private fun PlayingCard(card: Card, revealed: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .aspectRatio(0.7f)
            .background(
                if (revealed) BacchusColors.CardFace else BacchusColors.Surface,
                RoundedCornerShape(24.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (revealed) {
            val pipColor = if (card.suit == Suit.HEARTS || card.suit == Suit.DIAMONDS) {
                BacchusColors.CardRed
            } else {
                BacchusColors.CardInk
            }
            Text(
                text = "${card.rank.label}${suitSymbol(card.suit)}",
                style = MaterialTheme.typography.displayMedium,
                color = pipColor,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Text(
                text = "BACCHUS",
                style = MaterialTheme.typography.labelSmall,
                color = BacchusColors.InkMuted,
            )
        }
    }
}

private fun suitSymbol(suit: Suit): String = when (suit) {
    Suit.CLUBS -> "♣"
    Suit.DIAMONDS -> "♦"
    Suit.HEARTS -> "♥"
    Suit.SPADES -> "♠"
}

@Composable
private fun ContestControls(
    players: List<Player>,
    onEscalate: (Player) -> Unit,
    onResolve: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { players.firstOrNull()?.let(onEscalate) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.borderland_escalate))
        }
        Button(onClick = onResolve, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.borderland_resolve))
        }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.borderland_cancel_contest))
        }
    }
}
