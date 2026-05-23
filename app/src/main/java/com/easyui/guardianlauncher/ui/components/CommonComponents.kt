package com.easyui.guardianlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun PinKeypad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "⌫")
    )

    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowKeys.forEach { key ->
                    val testTag = when (key) {
                        "⌫" -> "pin_backspace"
                        "C" -> "pin_clear"
                        else -> "pin_digit_$key"
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.6f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                            .clickable {
                                when (key) {
                                    "C" -> onClearClick()
                                    "⌫" -> onDeleteClick()
                                    else -> onDigitClick(key)
                                }
                            }
                            .testTag(testTag)
                    ) {
                        Text(
                            text = key,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinEntryDialog(
    onDismissRequest: () -> Unit,
    onPinSubmitted: (String) -> Unit,
    errorMessage: String = "",
    titleText: String = "Parent PIN Required"
) {
    val pin = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // PIN dots indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(6) { index ->
                        val isFilled = index < pin.value.length
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                PinKeypad(
                    onDigitClick = { digit ->
                        if (pin.value.length < 6) {
                            pin.value = pin.value + digit
                        }
                    },
                    onDeleteClick = {
                        if (pin.value.isNotEmpty()) {
                            pin.value = pin.value.dropLast(1)
                        }
                    },
                    onClearClick = {
                        pin.value = ""
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onPinSubmitted(pin.value) },
                        enabled = pin.value.length >= 4
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

@Composable
fun ModeSelectItem(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
        RadioButton(selected = isSelected, onClick = onClick)
    }
}
