package com.easyui.guardianlauncher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.easyui.guardianlauncher.guardian.CheckState

@Composable
fun DefaultLauncherStatusCard(
    state: CheckState?,
    onOpenHomeSettings: () -> Unit,
    onCheckAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val normalized = state ?: CheckState.UNKNOWN
    val (icon, tint, title, subtitle) = when (normalized) {
        CheckState.OK -> Quad(
            Icons.Default.CheckCircle,
            MaterialTheme.colorScheme.primary,
            "Default launcher: Set",
            "Pressing HOME should return to EasyUI.",
        )
        CheckState.WARNING, CheckState.ACTION_REQUIRED -> Quad(
            Icons.Default.Error,
            MaterialTheme.colorScheme.error,
            "Default launcher: Not set",
            "Until EasyUI is set as the default home screen, pressing HOME may return to your phone’s normal launcher.",
        )
        CheckState.UNKNOWN -> Quad(
            Icons.Default.Info,
            MaterialTheme.colorScheme.outline,
            "Default launcher: Unknown",
            "This device didn’t report the default home app clearly. You can still set EasyUI as the Home app in Android Settings.",
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Black)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "EasyUI controls the child home screen, but Android settings and Google Family Link may still be needed for deeper restrictions.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onOpenHomeSettings,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Text("Set default home", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onCheckAgain,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Check again", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

