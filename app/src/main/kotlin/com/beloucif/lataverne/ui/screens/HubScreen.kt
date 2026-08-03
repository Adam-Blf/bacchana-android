package com.beloucif.lataverne.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beloucif.lataverne.R
import com.beloucif.lataverne.content.PackRepository
import com.beloucif.lataverne.content.PremiumCatalogEntry
import com.beloucif.lataverne.core.GameMode
import com.beloucif.lataverne.ui.theme.LaTaverneColors

/**
 * Bento grid of game modes. Free prompt packs are resolved at runtime from [PackRepository].
 * Premium packs ([premiumCatalog], metadata only - see [PackRepository]) render as locked
 * tiles that open the paywall; unlocking is entitlement-based (one purchase unlocks the whole
 * catalog), not a per-tile in-app purchase.
 */
@Composable
fun HubScreen(
    playerCount: Int,
    isPremium: Boolean,
    packRepository: PackRepository,
    premiumCatalog: List<PremiumCatalogEntry>,
    onSelectBorderland: () -> Unit,
    onSelectPromptMode: (GameMode) -> Unit,
    onSelectRoulette: () -> Unit,
    onSelectTribunal: () -> Unit,
    onSelectAuction: () -> Unit,
    onSelectQuiz: () -> Unit,
    onSelectRanking: () -> Unit,
    onSelectWouldYouRather: () -> Unit,
    onOpenPaywall: () -> Unit,
) {
    var freeModes by remember { mutableStateOf<List<GameMode>>(emptyList()) }

    LaunchedEffect(Unit) {
        val ids = packRepository.listFreePackIds()
        val modes = ids.mapNotNull { id -> packRepository.loadPack(id).getOrNull()?.pack?.mode }.distinct()
        // Tu preferes is embedded (voting engine, no content pack) - never rendered through the
        // prompt route even if a stale pack still ships it, wired here directly instead.
        freeModes = modes.filterNot { it == GameMode.WOULD_YOU_RATHER }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LaTaverneColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.hub_title),
            style = MaterialTheme.typography.headlineMedium,
            color = LaTaverneColors.Ink,
            modifier = Modifier.padding(vertical = 24.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                ModeTile(
                    title = stringResource(R.string.borderland_title),
                    locked = false,
                    minPlayers = 2,
                    playerCount = playerCount,
                    onClick = onSelectBorderland,
                )
            }
            items(freeModes) { mode ->
                ModeTile(
                    title = modeDisplayName(mode),
                    locked = false,
                    minPlayers = 2,
                    playerCount = playerCount,
                    onClick = { onSelectPromptMode(mode) },
                )
            }
            item {
                // La Roulette is embedded (no content pack in la-taverne-content), so it
                // never appears in `freeModes` - wired here directly, like Le Coupe-Gorge.
                ModeTile(
                    title = modeDisplayName(GameMode.ROULETTE),
                    locked = false,
                    minPlayers = 1,
                    playerCount = playerCount,
                    onClick = onSelectRoulette,
                )
            }
            item {
                // Le Pilori (Tribunal) is embedded too - each trial needs at least one accuser,
                // one accused and one voter left over, hence the higher player floor.
                ModeTile(
                    title = modeDisplayName(GameMode.TRIBUNAL),
                    locked = false,
                    minPlayers = 3,
                    playerCount = playerCount,
                    onClick = onSelectTribunal,
                )
            }
            item {
                // La Criée (Auction) is embedded too - the bidding voice needs at least a
                // bidder and a challenger, hence the 2-player floor like most prompt modes.
                ModeTile(
                    title = modeDisplayName(GameMode.AUCTION),
                    locked = false,
                    minPlayers = 2,
                    playerCount = playerCount,
                    onClick = onSelectAuction,
                )
            }
            item {
                // Quitte ou Trinque (Quiz) is embedded too - a solo cagnotte/turn loop, same
                // 2-player floor as most turn-based modes.
                ModeTile(
                    title = modeDisplayName(GameMode.QUIZ),
                    locked = false,
                    minPlayers = 2,
                    playerCount = playerCount,
                    onClick = onSelectQuiz,
                )
            }
            item {
                // Le Tableau d'Honneur (Ranking) is embedded too - a judge ranks the other
                // players, so it needs at least 3 contestants on top of the judge: 4-player floor.
                ModeTile(
                    title = modeDisplayName(GameMode.RANKING),
                    locked = false,
                    minPlayers = 4,
                    playerCount = playerCount,
                    onClick = onSelectRanking,
                )
            }
            item {
                // Tu preferes is embedded too - a real vote needs at least two camps to split,
                // same 2-player floor as most turn-based modes.
                ModeTile(
                    title = modeDisplayName(GameMode.WOULD_YOU_RATHER),
                    locked = false,
                    minPlayers = 2,
                    playerCount = playerCount,
                    onClick = onSelectWouldYouRather,
                )
            }
            if (!isPremium) {
                items(premiumCatalog, key = { it.id }) { entry ->
                    ModeTile(
                        title = entry.title,
                        locked = true,
                        minPlayers = 0,
                        playerCount = playerCount,
                        onClick = onOpenPaywall,
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeTile(
    title: String,
    locked: Boolean,
    minPlayers: Int,
    playerCount: Int,
    onClick: () -> Unit,
) {
    val disabled = !locked && playerCount < minPlayers
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(LaTaverneColors.Surface, RoundedCornerShape(24.dp))
            .clickable(enabled = !disabled, onClick = onClick)
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (locked) {
                Text(
                    text = stringResource(R.string.hub_premium_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = LaTaverneColors.Premium,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = LaTaverneColors.Ink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start),
            )
        }
        if (!locked) {
            Text(
                text = stringResource(R.string.hub_min_players, minPlayers),
                style = MaterialTheme.typography.labelSmall,
                color = LaTaverneColors.InkMuted,
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }
    }
}

private fun modeDisplayName(mode: GameMode): String = when (mode) {
    GameMode.BORDERLAND -> "Le Coupe-Gorge"
    GameMode.PICOLO -> "Le Taulier"
    GameMode.TRUTH_OR_DARE -> "Action ou Vérité"
    GameMode.NEVER_HAVE_I_EVER -> "Je n'ai jamais"
    GameMode.WHO_AMONG -> "Qui de nous"
    GameMode.WOULD_YOU_RATHER -> "Tu préfères"
    GameMode.ITS_A_10_BUT -> "C'est un 10 mais"
    GameMode.SEVEN_SECONDS -> "7 secondes"
    GameMode.TRIBUNAL -> "Le Pilori"
    GameMode.ROULETTE -> "La Roue du Destin"
    GameMode.AUCTION -> "La Criée"
    GameMode.QUIZ -> "Quitte ou Trinque"
    GameMode.RANKING -> "Le Tableau d'Honneur"
}
