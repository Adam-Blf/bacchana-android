package com.beloucif.meskova.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beloucif.meskova.R
import com.beloucif.meskova.core.Gender
import com.beloucif.meskova.core.Player
import com.beloucif.meskova.core.Relationship
import com.beloucif.meskova.ui.theme.MeskovaColors

private val GENDER_OPTIONS = listOf(
    Gender.M to R.string.gender_m,
    Gender.F to R.string.gender_f,
    Gender.X to R.string.gender_x,
)

private val RELATIONSHIP_OPTIONS = listOf(
    Relationship.SINGLE to R.string.relationship_single,
    Relationship.COUPLE to R.string.relationship_couple,
)

/** Player check-in, "liste d'inscription à l'arène". First screen of the app. */
@Composable
fun WelcomeScreen(
    players: List<Player>,
    onAddPlayer: (String) -> Unit,
    onRemovePlayer: (String) -> Unit,
    onSetPlayerAttributes: (playerId: String, gender: Gender?, relationship: Relationship?) -> Unit,
    onStart: () -> Unit,
) {
    var input by remember { mutableStateOf("") }
    // Un seul panneau de genre/statut ouvert a la fois - reste compact, jamais impose.
    var expandedPlayerId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskovaColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
    ) {
        Column(modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium,
                color = MeskovaColors.Neon,
            )
            Text(
                text = stringResource(R.string.welcome_slogan),
                style = MaterialTheme.typography.headlineLarge,
                color = MeskovaColors.Ink,
            )
            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MeskovaColors.InkSecondary,
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
                    .background(MeskovaColors.NeonDeep, RoundedCornerShape(12.dp)),
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.welcome_add_player), tint = MeskovaColors.Ink)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(players, key = { it.id }) { player ->
                PlayerRow(
                    player = player,
                    expanded = expandedPlayerId == player.id,
                    onToggleExpanded = {
                        expandedPlayerId = if (expandedPlayerId == player.id) null else player.id
                    },
                    onRemove = { onRemovePlayer(player.id) },
                    onSetGender = { gender -> onSetPlayerAttributes(player.id, gender, player.relationship) },
                    onSetRelationship = { relationship -> onSetPlayerAttributes(player.id, player.gender, relationship) },
                )
            }
        }

        if (players.size < 2) {
            Text(
                text = stringResource(R.string.welcome_min_players),
                style = MaterialTheme.typography.bodyMedium,
                color = MeskovaColors.InkMuted,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Text(
            text = stringResource(R.string.welcome_attributes_hint),
            style = MaterialTheme.typography.labelSmall,
            color = MeskovaColors.InkMuted,
            modifier = Modifier.padding(bottom = 8.dp),
        )

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
            color = MeskovaColors.InkMuted,
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
}

@Composable
private fun PlayerRow(
    player: Player,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onRemove: () -> Unit,
    onSetGender: (Gender?) -> Unit,
    onSetRelationship: (Relationship?) -> Unit,
) {
    val hasAttributes = player.gender != null || player.relationship != null
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MeskovaColors.Surface, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                color = MeskovaColors.Ink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            AttributesToggleButton(
                expandedOrSet = expanded || hasAttributes,
                contentDescription = stringResource(R.string.welcome_attributes_toggle, player.name),
                onClick = onToggleExpanded,
            )
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = stringResource(R.string.welcome_remove_player, player.name),
                    tint = MeskovaColors.InkMuted,
                )
            }
        }

        AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GENDER_OPTIONS.forEach { (value, labelRes) ->
                            val label = stringResource(labelRes)
                            AttributeChip(
                                label = label,
                                selected = player.gender == value,
                                onClick = { onSetGender(if (player.gender == value) null else value) },
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        RELATIONSHIP_OPTIONS.forEach { (value, labelRes) ->
                            val label = stringResource(labelRes)
                            AttributeChip(
                                label = label,
                                selected = player.relationship == value,
                                onClick = { onSetRelationship(if (player.relationship == value) null else value) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Compact "genre et statut" disclosure toggle, drawn as three bars (no icon library needed). */
@Composable
private fun AttributesToggleButton(
    expandedOrSet: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val tint = if (expandedOrSet) MeskovaColors.OrangeInk else MeskovaColors.InkMuted
    val borderColor = if (expandedOrSet) MeskovaColors.Neon else MeskovaColors.Border
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(44.dp)
            .border(1.dp, borderColor, RoundedCornerShape(percent = 50))
            .selectable(selected = expandedOrSet, onClick = onClick, role = Role.Button)
            .semantics(mergeDescendants = true) { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(Modifier.size(width = 14.dp, height = 2.dp).background(tint, RoundedCornerShape(1.dp)))
            Box(Modifier.size(width = 10.dp, height = 2.dp).background(tint, RoundedCornerShape(1.dp)))
            Box(Modifier.size(width = 14.dp, height = 2.dp).background(tint, RoundedCornerShape(1.dp)))
        }
    }
}

@Composable
private fun AttributeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
            .border(
                width = 1.dp,
                color = if (selected) MeskovaColors.Neon else MeskovaColors.Border,
                shape = RoundedCornerShape(percent = 50),
            )
            .background(
                if (selected) MeskovaColors.Neon.copy(alpha = 0.15f) else MeskovaColors.BgRaised,
                RoundedCornerShape(percent = 50),
            )
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Checkbox,
            )
            .semantics { this.selected = selected }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MeskovaColors.OrangeInk else MeskovaColors.InkMuted,
        )
    }
}
