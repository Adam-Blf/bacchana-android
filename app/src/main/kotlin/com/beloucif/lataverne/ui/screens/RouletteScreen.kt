package com.beloucif.lataverne.ui.screens

import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beloucif.lataverne.R
import com.beloucif.lataverne.core.ROULETTE_SEGMENTS
import com.beloucif.lataverne.core.RouletteSegment
import com.beloucif.lataverne.ui.theme.LaTaverneColors
import kotlin.math.ceil
import kotlin.random.Random
import kotlinx.coroutines.launch

// Aplats orange / jaune alternés, texte encre - palette néobrutaliste (mirrors WHEEL_COLORS
// in la-taverne/src/components/screens/RouletteScreen.tsx).
private val WHEEL_COLORS = listOf(LaTaverneColors.NeonSoft, Color(0xFFFFD029))
private const val SPIN_DURATION_MS = 3200
private val SPIN_EASING = CubicBezierEasing(0.17f, 0.67f, 0.12f, 0.99f)

/**
 * La Roue du Destin - embedded mode, no content pack, no named player, no recap.
 * Wheel of [ROULETTE_SEGMENTS], spins ~5 turns plus the offset to land on a random
 * segment centre, then freezes the result. Session closes on [onQuit] with a
 * `session_completed` analytics event (turns = number of spins played), never a recap.
 */
@Composable
fun RouletteScreen(onQuit: (spinsPlayed: Int) -> Unit) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val reduceMotion = remember {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }.getOrDefault(false)
    }

    val rotation = remember { Animatable(0f) }
    var spinning by remember { mutableStateOf(false) }
    var pendingIndex by remember { mutableStateOf<Int?>(null) }
    var resultIndex by remember { mutableStateOf<Int?>(null) }
    // La roue ne désigne pas nommément le joueur puni : on compte les tours plutôt
    // qu'une addition chiffrée, pour clôturer la session.
    var spinsPlayed by remember { mutableIntStateOf(0) }

    val segments = ROULETTE_SEGMENTS
    val segmentAngle = 360f / segments.size

    fun spin() {
        if (spinning) return
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)

        val index = Random.nextInt(segments.size)
        val targetCenter = index * segmentAngle + segmentAngle / 2f
        val base = ceil((rotation.value + 1f) / 360f) * 360f
        val nextRotation = base + 5 * 360f + (360f - targetCenter)

        pendingIndex = index
        resultIndex = null
        spinning = true

        scope.launch {
            if (reduceMotion) {
                rotation.snapTo(nextRotation)
            } else {
                rotation.animateTo(
                    targetValue = nextRotation,
                    animationSpec = tween(durationMillis = SPIN_DURATION_MS, easing = SPIN_EASING),
                )
            }
            spinning = false
            resultIndex = pendingIndex
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            spinsPlayed += 1
        }
    }

    val result = resultIndex?.let { segments[it] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LaTaverneColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            IconButton(
                onClick = { onQuit(spinsPlayed) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .background(LaTaverneColors.Surface, CircleShape)
                    .border(2.dp, LaTaverneColors.Ink, CircleShape)
                    .semantics { contentDescription = context.getString(R.string.roulette_quit_description) },
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = LaTaverneColors.Ink)
            }
        }

        Text(
            text = stringResource(R.string.roulette_title),
            style = MaterialTheme.typography.labelSmall,
            color = LaTaverneColors.InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp),
        )

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1f),
                contentAlignment = Alignment.Center,
            ) {
                // Fixed pointer at the top - never rotates.
                Canvas(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 0.dp)
                        .size(width = 24.dp, height = 18.dp),
                ) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width / 2f, size.height)
                        close()
                    }
                    drawPath(path, color = Color(0xFF111111))
                }

                RouletteWheel(
                    segments = segments,
                    rotationDegrees = rotation.value,
                    segmentAngle = segmentAngle,
                    modifier = Modifier.fillMaxSize(),
                )

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(LaTaverneColors.Surface, CircleShape)
                        .border(2.dp, LaTaverneColors.Ink, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(LaTaverneColors.Neon, CircleShape),
                    )
                }
            }
        }

        AnimatedVisibility(visible = result != null, enter = fadeIn()) {
            result?.let { segment -> RouletteResultCard(segment) }
        }

        Button(
            onClick = ::spin,
            enabled = !spinning,
            colors = ButtonDefaults.buttonColors(
                containerColor = LaTaverneColors.Neon,
                contentColor = LaTaverneColors.CardFace,
                disabledContainerColor = LaTaverneColors.NeonSoft,
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 24.dp)
                .height(56.dp)
                .border(2.dp, LaTaverneColors.Ink, RoundedCornerShape(16.dp)),
        ) {
            Text(
                text = if (spinning) stringResource(R.string.roulette_spinning) else stringResource(R.string.roulette_spin),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
private fun RouletteWheel(
    segments: List<RouletteSegment>,
    rotationDegrees: Float,
    segmentAngle: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .graphicsLayer { rotationZ = rotationDegrees }
            .background(LaTaverneColors.Surface, CircleShape)
            .border(4.dp, LaTaverneColors.Ink, CircleShape)
            .semantics {
                contentDescription = "Roue de la fortune, ${segments.size} segments"
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f
            segments.forEachIndexed { index, _ ->
                val startAngle = index * segmentAngle - 90f
                drawArc(
                    color = WHEEL_COLORS[index % WHEEL_COLORS.size],
                    startAngle = startAngle,
                    sweepAngle = segmentAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                )
                drawArc(
                    color = Color(0xFF111111),
                    startAngle = startAngle,
                    sweepAngle = segmentAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                    style = Stroke(width = 3.dp.toPx()),
                )
            }
        }

        segments.forEachIndexed { index, segment ->
            val angle = index * segmentAngle + segmentAngle / 2f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationZ = angle },
                contentAlignment = Alignment.TopCenter,
            ) {
                Text(
                    text = segment.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = LaTaverneColors.Ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(64.dp).padding(top = 20.dp),
                )
            }
        }
    }
}

@Composable
private fun RouletteResultCard(segment: RouletteSegment) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        // Hard shadow trick: an offset ink plate behind the face, no blur - neobrutalist.
        // matchParentSize does not contribute to the Box's own measurement, so the Box
        // sizes itself on the foreground Column below and this plate just mirrors it.
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(LaTaverneColors.Ink, RoundedCornerShape(20.dp)),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LaTaverneColors.CardFace, RoundedCornerShape(20.dp))
                .border(2.dp, LaTaverneColors.Ink, RoundedCornerShape(20.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = segment.label,
                style = MaterialTheme.typography.headlineMedium,
                color = LaTaverneColors.CardRed,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = segment.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = LaTaverneColors.CardInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
