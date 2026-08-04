package com.beloucif.lataverne.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.beloucif.lataverne.R
import com.beloucif.lataverne.ui.theme.LaTaverneColors
import com.beloucif.lataverne.ui.theme.ThemePreference
import com.beloucif.lataverne.ui.theme.resolve
import kotlinx.coroutines.launch

/**
 * Ecran Reglages : consolide apparence, premium, confidentialite, legal, a propos et
 * reinitialisation - ne reimplemente rien, chaque section delegue au store ou au repository
 * existant (ThemeStore via [onToggleTheme], EntitlementRepository via [onRestorePurchases],
 * ConsentStore via [onSetAnalyticsConsent]). Mirrors SettingsScreen.tsx sur le web.
 *
 * "Mes regles" (contenu personnalise) n'a pas d'equivalent natif sur Android pour le moment,
 * donc pas de section Contenu ici. Legal renvoie vers les pages web (pas de mentions/CGU/
 * confidentialite natives sur cette plateforme) plutot que de dupliquer un contenu juridique.
 */
@Composable
fun SettingsScreen(
    themePreference: ThemePreference,
    onToggleTheme: () -> Unit,
    isPremium: Boolean,
    billingEnabled: Boolean,
    onOpenPaywall: () -> Unit,
    onRestorePurchases: suspend () -> Boolean,
    analyticsEnabled: Boolean,
    onSetAnalyticsConsent: (Boolean) -> Unit,
    appVersionName: String,
    onResetTablee: () -> Unit,
    onBack: () -> Unit,
) {
    val isDark = themePreference.resolve()
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    var restoring by remember { mutableStateOf(false) }
    var restoreStatus by remember { mutableStateOf<String?>(null) }
    var confirmReset by remember { mutableStateOf(false) }

    val comingSoon = stringResource(R.string.premium_coming_soon)
    val restoreSuccess = stringResource(R.string.settings_restore_success)
    val restoreNone = stringResource(R.string.settings_restore_none)

    fun handleRestore() {
        if (restoring) return
        if (!billingEnabled) {
            restoreStatus = comingSoon
            return
        }
        restoring = true
        restoreStatus = null
        scope.launch {
            val restored = onRestorePurchases()
            restoring = false
            restoreStatus = if (restored) restoreSuccess else restoreNone
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LaTaverneColors.Bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.defaultMinSize(minWidth = 44.dp, minHeight = 44.dp),
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.settings_back_description),
                    tint = LaTaverneColors.Ink,
                )
            }
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall,
                color = LaTaverneColors.Ink,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                SettingsSection(title = stringResource(R.string.settings_section_appearance)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LaTaverneColors.Surface, RoundedCornerShape(16.dp))
                            .clickable(onClick = onToggleTheme)
                            .defaultMinSize(minHeight = 52.dp)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(
                                if (isDark) R.string.settings_theme_dark_label else R.string.settings_theme_light_label,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LaTaverneColors.Ink,
                        )
                        Text(
                            text = stringResource(R.string.settings_theme_change),
                            style = MaterialTheme.typography.labelSmall,
                            color = LaTaverneColors.InkSecondary,
                        )
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_premium)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LaTaverneColors.Surface, RoundedCornerShape(16.dp))
                                .defaultMinSize(minHeight = 52.dp)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.settings_premium_status_label),
                                style = MaterialTheme.typography.bodyMedium,
                                color = LaTaverneColors.Ink,
                            )
                            Text(
                                text = stringResource(
                                    if (isPremium) R.string.settings_premium_status_active else R.string.settings_premium_status_guest,
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPremium) LaTaverneColors.Premium else LaTaverneColors.InkSecondary,
                            )
                        }

                        if (!isPremium) {
                            Button(onClick = onOpenPaywall, modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(R.string.settings_premium_unlock))
                            }
                        }

                        TextButton(onClick = ::handleRestore, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (restoring) {
                                    stringResource(R.string.paywall_restoring)
                                } else {
                                    stringResource(R.string.paywall_restore)
                                },
                                color = LaTaverneColors.InkSecondary,
                            )
                        }
                        restoreStatus?.let { status ->
                            Text(
                                text = status,
                                style = MaterialTheme.typography.bodySmall,
                                color = LaTaverneColors.InkSecondary,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_privacy)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LaTaverneColors.Surface, RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.settings_analytics_label),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LaTaverneColors.Ink,
                                )
                                Text(
                                    text = stringResource(R.string.settings_analytics_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LaTaverneColors.InkSecondary,
                                )
                            }
                            Switch(
                                checked = analyticsEnabled,
                                onCheckedChange = onSetAnalyticsConsent,
                                colors = SwitchDefaults.colors(checkedThumbColor = LaTaverneColors.Neon),
                            )
                        }

                        TextButton(
                            onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.settings_privacy_policy), color = LaTaverneColors.InkSecondary)
                        }
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_legal)) {
                    TextButton(
                        onClick = { uriHandler.openUri(LEGAL_URL) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.settings_legal_link), color = LaTaverneColors.InkSecondary)
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_about)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LaTaverneColors.Surface, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            color = LaTaverneColors.Ink,
                        )
                        Text(
                            text = stringResource(R.string.settings_about_version, appVersionName),
                            style = MaterialTheme.typography.labelSmall,
                            color = LaTaverneColors.InkSecondary,
                        )
                        Text(
                            text = stringResource(R.string.settings_about_publisher),
                            style = MaterialTheme.typography.bodySmall,
                            color = LaTaverneColors.InkSecondary,
                        )
                    }
                }
            }

            item {
                SettingsSection(title = stringResource(R.string.settings_section_reset)) {
                    Column {
                        Button(
                            onClick = { confirmReset = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.settings_reset_cta))
                        }
                        Text(
                            text = stringResource(R.string.settings_reset_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = LaTaverneColors.InkSecondary,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text(stringResource(R.string.settings_reset_dialog_title)) },
            text = { Text(stringResource(R.string.settings_reset_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    onResetTablee()
                    confirmReset = false
                }) {
                    Text(stringResource(R.string.settings_reset_dialog_confirm), color = LaTaverneColors.CardRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) {
                    Text(stringResource(R.string.settings_reset_dialog_cancel))
                }
            },
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = LaTaverneColors.InkMuted,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}

/**
 * Pas d'ecrans legaux natifs sur Android (contrairement au web, voir MentionsLegalesScreen /
 * CguScreen / ConfidentialiteScreen dans le repo la-taverne) : on renvoie vers les pages du
 * site plutot que de dupliquer un contenu juridique qui doit rester une seule source de verite.
 */
private const val PRIVACY_POLICY_URL = "https://lataverne.beloucif.com"
private const val LEGAL_URL = "https://lataverne.beloucif.com"
