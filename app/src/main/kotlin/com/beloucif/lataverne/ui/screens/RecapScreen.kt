package com.beloucif.lataverne.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beloucif.lataverne.R
import com.beloucif.lataverne.core.Player
import com.beloucif.lataverne.ui.theme.LaTaverneColors

/** Podium + tabular-nums stats at the end of a session. */
@Composable
fun RecapScreen(
    players: List<Player>,
    onReplay: () -> Unit,
    onBackToHub: () -> Unit,
) {
    val ranked = players.sortedByDescending { it.penaltiesStandard + it.penaltiesMajor * 3 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LaTaverneColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.recap_title),
            style = MaterialTheme.typography.headlineMedium,
            color = LaTaverneColors.Neon,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(ranked, key = { it.id }) { player ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LaTaverneColors.Surface, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = LaTaverneColors.Ink,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(
                            R.string.recap_total_penalties,
                            player.penaltiesStandard + player.penaltiesMajor,
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = LaTaverneColors.Premium,
                    )
                }
            }
        }

        Button(onClick = onReplay, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text(stringResource(R.string.recap_replay))
        }
        OutlinedButton(onClick = onBackToHub, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(stringResource(R.string.recap_back_to_hub))
        }
    }
}
