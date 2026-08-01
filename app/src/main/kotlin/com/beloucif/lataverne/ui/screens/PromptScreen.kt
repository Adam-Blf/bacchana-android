package com.beloucif.lataverne.ui.screens

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
import com.beloucif.lataverne.R
import com.beloucif.lataverne.core.GameMode
import com.beloucif.lataverne.core.Player
import com.beloucif.lataverne.ui.PromptUiState
import com.beloucif.lataverne.ui.PromptViewModel
import com.beloucif.lataverne.ui.theme.LaTaverneColors

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
            .background(LaTaverneColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.labelSmall,
            color = LaTaverneColors.InkMuted,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                PromptUiState.Loading -> Text(
                    text = "…",
                    style = MaterialTheme.typography.displayMedium,
                    color = LaTaverneColors.InkMuted,
                )

                PromptUiState.Empty -> Text(
                    text = stringResource(R.string.prompt_no_more_players),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LaTaverneColors.InkSecondary,
                )

                PromptUiState.Finished -> Text(
                    text = stringResource(R.string.borderland_game_over),
                    style = MaterialTheme.typography.headlineLarge,
                    color = LaTaverneColors.Ink,
                )

                is PromptUiState.Ready -> Column {
                    Text(
                        text = state.draw.text,
                        style = MaterialTheme.typography.headlineMedium,
                        color = LaTaverneColors.Ink,
                        fontWeight = FontWeight.Bold,
                    )
                    if (state.activeRules.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.prompt_active_rules),
                            style = MaterialTheme.typography.labelSmall,
                            color = LaTaverneColors.Premium,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                        state.activeRules.forEach { rule ->
                            Text(
                                text = "${rule.target.name} - ${rule.text}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LaTaverneColors.InkSecondary,
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
                    .background(LaTaverneColors.NeonDeep, RoundedCornerShape(16.dp)),
            ) {
                Text(stringResource(R.string.prompt_next))
            }
        }
    }
}
