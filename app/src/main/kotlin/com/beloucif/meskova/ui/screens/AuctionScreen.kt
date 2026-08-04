package com.beloucif.meskova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.beloucif.meskova.core.AUCTION_THEMES
import com.beloucif.meskova.core.AuctionTheme
import com.beloucif.meskova.ui.theme.MeskovaColors
import kotlinx.coroutines.delay

private const val CHALLENGE_SECONDS = 60

private enum class AuctionPhase { BIDDING, CHALLENGE, RESULT }

private fun pickTheme(pool: List<AuctionTheme>, exclude: String?): AuctionTheme {
    val candidates = pool.filter { it.id != exclude }
    val source = if (candidates.isNotEmpty()) candidates else pool
    return source.random()
}

/**
 * La Criée - embedded mode, no content pack, no named player, no recap. A theme is drawn,
 * the table bids out loud how many they can name in a minute, until someone shouts
 * "tu mens !" - the last bidder then has [CHALLENGE_SECONDS] to actually name that many.
 * Time runs out (or the challenger stops early with too few) = failure, the bidder takes
 * [bid] penalties; success = the accuser takes them instead. Session closes on [onQuit]
 * with `session_completed` (turns = rounds played), never a recap, never a mutated Player.
 */
@Composable
fun AuctionScreen(onQuit: (roundsPlayed: Int) -> Unit) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var theme by remember { mutableStateOf(pickTheme(AUCTION_THEMES, null)) }
    var phase by remember { mutableStateOf(AuctionPhase.BIDDING) }
    var roundsPlayed by remember { mutableIntStateOf(0) }
    var bid by remember { mutableIntStateOf(0) }
    var cited by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(CHALLENGE_SECONDS) }
    var success by remember { mutableStateOf<Boolean?>(null) }

    fun resolveChallenge(won: Boolean) {
        success = won
        phase = AuctionPhase.RESULT
        roundsPlayed += 1
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Décompte du défi : la coroutine est portée par cette recomposition de phase, elle
    // s'annule automatiquement dès que la phase change (résolution manuelle ou sortie).
    LaunchedEffect(phase) {
        if (phase != AuctionPhase.CHALLENGE) return@LaunchedEffect
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft = (secondsLeft - 1).coerceAtLeast(0)
        }
        resolveChallenge(false)
    }

    fun startChallenge() {
        if (bid < 1) return
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        cited = 0
        secondsLeft = CHALLENGE_SECONDS
        success = null
        phase = AuctionPhase.CHALLENGE
    }

    fun cite() {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        val next = cited + 1
        cited = next
        if (next >= bid) {
            resolveChallenge(true)
        }
    }

    fun nextRound() {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        val pool = AUCTION_THEMES
        theme = pickTheme(pool, theme.id)
        bid = 0
        cited = 0
        secondsLeft = CHALLENGE_SECONDS
        success = null
        phase = AuctionPhase.BIDDING
    }

    fun finish() {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        onQuit(roundsPlayed)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskovaColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            IconButton(
                onClick = { onQuit(roundsPlayed) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .background(MeskovaColors.Surface, CircleShape)
                    .border(2.dp, MeskovaColors.Ink, CircleShape)
                    .semantics { contentDescription = context.getString(R.string.auction_quit_description) },
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = MeskovaColors.Ink)
            }
        }

        Text(
            text = stringResource(R.string.auction_title),
            style = MaterialTheme.typography.labelSmall,
            color = MeskovaColors.InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp),
        )

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuctionThemeCard(theme)

            when (phase) {
                AuctionPhase.BIDDING -> AuctionBidding(bid = bid, onDecrease = { bid = (bid - 1).coerceAtLeast(0) }, onIncrease = { bid += 1 })
                AuctionPhase.CHALLENGE -> AuctionChallenge(
                    secondsLeft = secondsLeft,
                    cited = cited,
                    bid = bid,
                    onDecrease = { cited = (cited - 1).coerceAtLeast(0) },
                    onCite = ::cite,
                )
                AuctionPhase.RESULT -> AuctionResult(success = success == true, bid = bid, cited = cited)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (phase) {
                AuctionPhase.BIDDING -> {
                    AuctionPrimaryButton(
                        text = stringResource(R.string.auction_start_challenge),
                        enabled = bid >= 1,
                        onClick = ::startChallenge,
                    )
                    OutlinedButton(
                        onClick = ::nextRound,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, MeskovaColors.Ink),
                    ) {
                        Text(stringResource(R.string.auction_change_theme), color = MeskovaColors.Ink)
                    }
                }

                AuctionPhase.CHALLENGE -> OutlinedButton(
                    onClick = { resolveChallenge(cited >= bid) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, MeskovaColors.Ink),
                ) {
                    Text(stringResource(R.string.auction_stop_challenge), color = MeskovaColors.Ink)
                }

                AuctionPhase.RESULT -> {
                    AuctionPrimaryButton(text = stringResource(R.string.auction_new_theme), onClick = ::nextRound)
                    OutlinedButton(
                        onClick = ::finish,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, MeskovaColors.Ink),
                    ) {
                        Text(stringResource(R.string.auction_finish), color = MeskovaColors.Ink)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuctionThemeCard(theme: AuctionTheme) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MeskovaColors.CardFace, RoundedCornerShape(24.dp))
            .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.auction_theme_label),
            style = MaterialTheme.typography.labelSmall,
            color = MeskovaColors.InkMuted,
        )
        Text(
            text = theme.text,
            style = MaterialTheme.typography.headlineSmall,
            color = MeskovaColors.CardInk,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun AuctionBidding(bid: Int, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = stringResource(R.string.auction_bidding_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MeskovaColors.InkSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StepperButton(
                label = "−",
                description = stringResource(R.string.auction_decrease_bid),
                background = MeskovaColors.Surface,
                onClick = onDecrease,
            )
            Text(
                text = bid.toString(),
                style = MaterialTheme.typography.displayMedium,
                color = MeskovaColors.Ink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(96.dp),
                textAlign = TextAlign.Center,
            )
            StepperIconButton(
                icon = Icons.Filled.Add,
                description = stringResource(R.string.auction_increase_bid),
                background = MeskovaColors.Premium,
                onClick = onIncrease,
            )
        }
    }
}

@Composable
private fun AuctionChallenge(secondsLeft: Int, cited: Int, bid: Int, onDecrease: () -> Unit, onCite: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = stringResource(R.string.auction_seconds_left, secondsLeft),
            style = MaterialTheme.typography.displayMedium,
            color = if (secondsLeft <= 10) MeskovaColors.CardRed else MeskovaColors.Ink,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.auction_challenge_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MeskovaColors.InkSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 20.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StepperButton(
                label = "−",
                description = stringResource(R.string.auction_decrease_cited),
                background = MeskovaColors.Surface,
                onClick = onDecrease,
            )
            Text(
                text = stringResource(R.string.auction_cited_ratio, cited, bid),
                style = MaterialTheme.typography.displayMedium,
                color = MeskovaColors.Ink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(120.dp),
                textAlign = TextAlign.Center,
            )
            StepperIconButton(
                icon = Icons.Filled.Add,
                description = stringResource(R.string.auction_increase_cited),
                background = MeskovaColors.Success,
                onClick = onCite,
            )
        }
    }
}

@Composable
private fun AuctionResult(success: Boolean, bid: Int, cited: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .background(if (success) MeskovaColors.Success else MeskovaColors.CardRed, RoundedCornerShape(24.dp))
            .border(2.dp, MeskovaColors.Ink, RoundedCornerShape(24.dp))
            .padding(28.dp)
            .semantics { contentDescription = if (success) "Pari tenu" else "Ça sentait le bluff" },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(if (success) R.string.auction_result_success_title else R.string.auction_result_failure_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MeskovaColors.CardFace,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = if (success) {
                stringResource(R.string.auction_result_success_body, bid, bid, bid)
            } else {
                stringResource(R.string.auction_result_failure_body, cited, bid, bid)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MeskovaColors.CardFace,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun StepperButton(label: String, description: String, background: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(background, CircleShape)
            .border(2.dp, MeskovaColors.Ink, CircleShape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = MaterialTheme.typography.headlineSmall, color = MeskovaColors.Ink, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StepperIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    background: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(background, CircleShape)
            .border(2.dp, MeskovaColors.Ink, CircleShape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = MeskovaColors.Ink)
    }
}

@Composable
private fun AuctionPrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MeskovaColors.Neon,
            contentColor = MeskovaColors.CardFace,
            disabledContainerColor = MeskovaColors.NeonSoft,
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
