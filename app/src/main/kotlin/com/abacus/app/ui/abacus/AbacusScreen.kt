package com.abacus.app.ui.abacus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abacus.app.model.Operation
import com.abacus.app.ui.theme.*
import com.abacus.app.util.NumberFormatter

/**
 * Main abacus screen content (used inside the NavHost; no inner Scaffold).
 * Hosts the interactive board, real-time value display, calculator panel,
 * and session history.
 */
@Composable
fun AbacusScreenContent(vm: AbacusViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    LazyColumn(
        modifier        = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentPadding  = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ── Board ─────────────────────────────────────────────────────────────
        item {
            AbacusBoard(
                rods           = state.rods,
                onHeavenToggle = vm::toggleHeavenBead,
                onEarthToggle  = vm::toggleEarthBead
            )
        }

        // ── Value Display Card ────────────────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = WoodBrown),
                shape    = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text  = "Current Value",
                        color = TextLight.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text       = NumberFormatter.formatWithCommas(state.currentValue),
                        color      = HeavenBead,
                        fontSize   = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text      = NumberFormatter.toWords(state.currentValue),
                        color     = TextLight.copy(alpha = 0.9f),
                        style     = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ── Calculator Panel ──────────────────────────────────────────────────
        item {
            CalculatorPanel(
                savedValue        = state.savedValue,
                selectedOperation = state.selectedOperation,
                resultMessage     = state.resultMessage,
                onSave            = vm::saveFirstNumber,
                onOperation       = vm::selectOperation,
                onCalculate       = vm::calculate,
                onReset           = vm::reset
            )
        }

        // ── History ───────────────────────────────────────────────────────────
        if (state.history.isNotEmpty()) {
            item {
                Text(
                    text       = "📋 History",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextDark
                )
            }
            items(state.history) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = Color.White),
                    shape    = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        text     = record.expression,
                        modifier = Modifier.padding(12.dp),
                        style    = MaterialTheme.typography.bodyLarge,
                        color    = TextDark
                    )
                }
            }
        }

        // Bottom padding
        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CalculatorPanel(
    savedValue:        Long?,
    selectedOperation: Operation?,
    resultMessage:     String?,
    onSave:            () -> Unit,
    onOperation:       (Operation) -> Unit,
    onCalculate:       () -> Unit,
    onReset:           () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text       = "🔢 Calculator",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = TextDark
            )

            // Saved-value indicator
            if (savedValue != null) {
                Text(
                    text  = "① Saved: ${NumberFormatter.formatWithCommas(savedValue)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Step 1 – save / reset
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors  = ButtonDefaults.buttonColors(containerColor = WoodBrown)
                ) {
                    Text(
                        text  = if (savedValue == null) "① Save №" else "① Re-save №",
                        color = TextLight
                    )
                }
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("↺ Reset", color = AccentRed)
                }
            }

            // Step 2 – operation
            Text(
                text  = "② Choose operation:",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Operation.values().forEach { op ->
                    val selected = op == selectedOperation
                    Button(
                        onClick  = { onOperation(op) },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = if (selected) WoodBrown else Color(0xFFEEEEEE)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text     = op.symbol,
                            color    = if (selected) HeavenBead else TextDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Step 3 – set second number, calculate
            Text(
                text  = "③ Set second number on board, then:",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )
            Button(
                onClick  = onCalculate,
                modifier = Modifier.fillMaxWidth(),
                enabled  = savedValue != null && selectedOperation != null,
                colors   = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Text(
                    text       = "= Calculate!",
                    color      = Color.White,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Result banner
            AnimatedVisibility(
                visible = resultMessage != null,
                enter   = fadeIn() + slideInVertically { it / 2 },
                exit    = fadeOut()
            ) {
                resultMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentGreen.copy(alpha = 0.12f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text       = "✅  $msg",
                            style      = MaterialTheme.typography.bodyLarge,
                            color      = AccentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
