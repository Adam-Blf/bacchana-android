package com.beloucif.bacchus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beloucif.bacchus.R
import com.beloucif.bacchus.analytics.AnalyticsTracker
import com.beloucif.bacchus.content.PremiumCatalogEntry
import com.beloucif.bacchus.core.PremiumPlan
import com.beloucif.bacchus.ui.theme.BacchusColors
import kotlinx.coroutines.launch

/**
 * "Bacchus Premium" paywall: 3 plans (lifetime highlighted as the default, our
 * differentiator against subscription-only competitors), restore purchases, and full price
 * transparency - no misleading free trial mention. Without a RevenueCat key
 * (BuildConfig.BILLING_ENABLED == false), the purchase button is disabled and shows
 * "Bientot disponible", exactly like the web paywall.
 */
@Composable
fun PaywallScreen(
    billingEnabled: Boolean,
    premiumCatalog: List<PremiumCatalogEntry>,
    analyticsTracker: AnalyticsTracker,
    onPurchase: suspend (PremiumPlan) -> Boolean,
    onRestore: suspend () -> Boolean,
    onClose: () -> Unit,
) {
    var selectedPlan by remember { mutableStateOf(PremiumPlan.LIFETIME) }
    var purchasing by remember { mutableStateOf(false) }
    var restoring by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { analyticsTracker.trackEvent("paywall_shown") }

    fun close() {
        analyticsTracker.trackEvent("paywall_dismissed")
        onClose()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BacchusColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(BacchusColors.Premium.copy(alpha = 0.12f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = BacchusColors.Premium)
            }
            IconButton(onClick = ::close) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.paywall_close), tint = BacchusColors.InkMuted)
            }
        }

        Text(
            text = stringResource(R.string.paywall_title),
            style = MaterialTheme.typography.displayMedium,
            // Orange accent, jamais l'encre : en clair l'encre finit noir sur bordure noire,
            // en sombre blanc sur fond quasi blanc - illisible dans les deux cas. Neon est
            // recalcule par theme et reste AA sur SurfaceElevated (voir Color.kt).
            color = BacchusColors.Neon,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.paywall_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = BacchusColors.InkSecondary,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.paywall_included_title),
                    style = MaterialTheme.typography.labelLarge,
                    // InkSecondary, pas InkMuted : sur SurfaceElevated en sombre InkMuted tombe
                    // sous l'AA pour du texte petit. Mirrors PremiumPaywallModal.tsx (text-ink-secondary).
                    color = BacchusColors.InkSecondary,
                )
            }
            items(premiumCatalog, key = { it.id }) { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(entry.title, style = MaterialTheme.typography.bodyMedium, color = BacchusColors.Ink)
                    Text(
                        stringResource(R.string.paywall_item_count, entry.itemCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = BacchusColors.InkSecondary,
                    )
                }
            }
            item {
                Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PremiumPlan.entries.forEach { plan ->
                        PlanRow(
                            plan = plan,
                            selected = selectedPlan == plan,
                            onSelect = { selectedPlan = plan },
                        )
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.paywall_transparency),
                    style = MaterialTheme.typography.labelSmall,
                    color = BacchusColors.InkSecondary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        Button(
            onClick = {
                if (!billingEnabled || purchasing) return@Button
                analyticsTracker.trackEvent("purchase_started", mapOf("plan" to selectedPlan.productId))
                purchasing = true
                scope.launch {
                    val success = onPurchase(selectedPlan)
                    purchasing = false
                    if (success) {
                        analyticsTracker.trackEvent("purchase_completed", mapOf("plan" to selectedPlan.productId))
                        onClose()
                    }
                }
            },
            enabled = billingEnabled && !purchasing,
            modifier = Modifier.fillMaxWidth().size(56.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (purchasing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BacchusColors.Bg)
            } else {
                Text(
                    text = if (billingEnabled) {
                        stringResource(R.string.paywall_purchase_cta, selectedPlan.priceLabel)
                    } else {
                        stringResource(R.string.premium_coming_soon)
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        TextButton(
            onClick = {
                if (restoring) return@TextButton
                restoring = true
                scope.launch {
                    val restored = onRestore()
                    restoring = false
                    if (restored) {
                        analyticsTracker.trackEvent("restore_completed")
                        onClose()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        ) {
            Text(
                text = if (restoring) stringResource(R.string.paywall_restoring) else stringResource(R.string.paywall_restore),
                color = BacchusColors.InkSecondary,
            )
        }
    }
}

@Composable
private fun PlanRow(plan: PremiumPlan, selected: Boolean, onSelect: () -> Unit) {
    val isBestValue = plan == PremiumPlan.LIFETIME
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) BacchusColors.Premium.copy(alpha = 0.10f) else BacchusColors.Surface,
                RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(plan.label, style = MaterialTheme.typography.titleMedium, color = BacchusColors.Ink, fontWeight = FontWeight.Bold)
                if (isBestValue) {
                    Text(
                        text = stringResource(R.string.paywall_best_value_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = BacchusColors.Premium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
        Text(plan.priceLabel, style = MaterialTheme.typography.titleLarge, color = BacchusColors.Ink)
    }
}
