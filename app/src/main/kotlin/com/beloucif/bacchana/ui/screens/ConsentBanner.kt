package com.beloucif.bacchana.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.beloucif.bacchana.R
import com.beloucif.bacchana.ui.theme.BacchanaColors

/**
 * RGPD consent banner for analytics (CLAUDE.md section 18): accept and decline are equally
 * prominent buttons, decline is not the visually weaker option, and nothing is pre-checked.
 * Shown once, until [ConsentStore.hasDecided] becomes true.
 */
@Composable
fun ConsentBanner(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BacchanaColors.SurfaceElevated, RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(R.string.consent_title),
            style = MaterialTheme.typography.titleMedium,
            color = BacchanaColors.Ink,
        )
        Text(
            text = stringResource(R.string.consent_body),
            style = MaterialTheme.typography.bodySmall,
            color = BacchanaColors.InkSecondary,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(onClick = onDecline, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.consent_decline))
            }
            Button(onClick = onAccept, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.consent_accept))
            }
        }
    }
}
