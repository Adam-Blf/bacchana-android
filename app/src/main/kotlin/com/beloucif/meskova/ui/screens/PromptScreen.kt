package com.beloucif.meskova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.beloucif.meskova.R
import com.beloucif.meskova.core.GameMode
import com.beloucif.meskova.core.Player
import com.beloucif.meskova.ui.PromptUiState
import com.beloucif.meskova.ui.PromptViewModel
import com.beloucif.meskova.ui.theme.MeskovaColors

/** Generic turn-based prompt screen shared by picolo, action-verite, tu-preferes, etc. */
@Composable
fun PromptScreen(
    mode: GameMode,
    players: List<Player>,
    viewModel: PromptViewModel,
    onExit: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mode, players) {
        if (players.size >= 2) viewModel.start(mode, players)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskovaColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.labelSmall,
            color = MeskovaColors.InkMuted,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                PromptUiState.Loading -> Text(
                    text = "…",
                    style = MaterialTheme.typography.displayMedium,
                    color = MeskovaColors.InkMuted,
                )

                PromptUiState.Empty -> Text(
                    text = stringResource(R.string.prompt_no_more_players),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MeskovaColors.InkSecondary,
                )

                PromptUiState.Finished -> Text(
                    text = stringResource(R.string.borderland_game_over),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MeskovaColors.Ink,
                )

                is PromptUiState.Ready -> Column {
                    Text(
                        text = state.draw.text,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MeskovaColors.Ink,
                        fontWeight = FontWeight.Bold,
                    )
                    targetLabel(state.targetPlayers)?.let { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = MeskovaColors.OrangeInk,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                    if (state.activeRules.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.prompt_active_rules),
                            style = MaterialTheme.typography.labelSmall,
                            color = MeskovaColors.Premium,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                        state.activeRules.forEach { rule ->
                            Text(
                                text = "${rule.target.name} - ${rule.text}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MeskovaColors.InkSecondary,
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = viewModel::drawNext,
                enabled = uiState is PromptUiState.Ready,
                modifier = Modifier
                    .weight(1f)
                    .background(MeskovaColors.NeonDeep, RoundedCornerShape(16.dp)),
            ) {
                Text(stringResource(R.string.prompt_next))
            }
        }
    }
}

/**
 * Builds the "C'est à X de jouer" hint for a resolved target (genre-m/f, single, couple,
 * pair). Returns null when nothing resolves - an item without a resolvable `targets` never
 * shows this line.
 */
@Composable
private fun targetLabel(targetPlayers: List<Player>): String? = when (targetPlayers.size) {
    0 -> null
    1 -> stringResource(R.string.prompt_target_single, targetPlayers[0].name)
    else -> stringResource(R.string.prompt_target_pair, targetPlayers[0].name, targetPlayers[1].name)
}
