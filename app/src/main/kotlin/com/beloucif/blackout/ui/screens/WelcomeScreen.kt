package com.beloucif.blackout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beloucif.blackout.R
import com.beloucif.blackout.core.Player
import com.beloucif.blackout.ui.theme.BlackOutColors

/** Player check-in, "liste d'inscription à l'arène". First screen of the app. */
@Composable
fun WelcomeScreen(
    players: List<Player>,
    onAddPlayer: (String) -> Unit,
    onRemovePlayer: (String) -> Unit,
    onStart: () -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackOutColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
    ) {
        Column(modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium,
                color = BlackOutColors.Neon,
            )
            Text(
                text = stringResource(R.string.welcome_slogan),
                style = MaterialTheme.typography.headlineLarge,
                color = BlackOutColors.Ink,
            )
            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = BlackOutColors.InkSecondary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text(stringResource(R.string.welcome_player_hint)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = {
                    onAddPlayer(input)
                    input = ""
                },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
                    .background(BlackOutColors.NeonDeep, RoundedCornerShape(12.dp)),
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.welcome_add_player), tint = BlackOutColors.Ink)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(players, key = { it.id }) { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BlackOutColors.Surface, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = BlackOutColors.Ink,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = { onRemovePlayer(player.id) }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(R.string.welcome_remove_player, player.name),
                            tint = BlackOutColors.InkMuted,
                        )
                    }
                }
            }
        }

        if (players.size < 2) {
            Text(
                text = stringResource(R.string.welcome_min_players),
                style = MaterialTheme.typography.bodyMedium,
                color = BlackOutColors.InkMuted,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Button(
                onClick = onStart,
                enabled = players.size >= 2,
                modifier = Modifier.fillMaxWidth().size(56.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.welcome_start),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        Text(
            text = stringResource(R.string.welcome_disclaimer),
            style = MaterialTheme.typography.labelSmall,
            color = BlackOutColors.InkMuted,
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
}
