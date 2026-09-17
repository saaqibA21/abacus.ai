package com.abacus.app.ui.learn

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.abacus.app.ui.abacus.AbacusBoard
import com.abacus.app.ui.abacus.AbacusViewModel
import com.abacus.app.ui.theme.*
import com.abacus.app.util.NumberFormatter

/** Ordered list of target numbers for the guided challenges. */
private val CHALLENGES = listOf(
    3L, 7L, 5L, 12L, 25L, 48L, 63L, 99L, 100L,
    256L, 500L, 999L, 1_234L, 5_678L, 9_999L,
    12_345L, 99_999L, 100_000L, 123_456L, 1_000_000L
)

/**
 * Learn screen: shows a target number, the user manipulates the abacus board
 * to match it, then taps "Check".  A hint animates the correct configuration.
 *
 * Uses its own independent [AbacusViewModel] instance (separate from the
 * free-play Abacus screen).
 */
@Composable
fun LearnScreenContent(vm: AbacusViewModel = viewModel()) {
    val boardState by vm.uiState.collectAsState()

    var challengeIdx   by remember { mutableStateOf(0) }
    var feedback       by remember { mutableStateOf<String?>(null) }
    var score          by remember { mutableStateOf(0) }
    var totalAttempts  by remember { mutableStateOf(0) }

    val target = CHALLENGES[challengeIdx % CHALLENGES.size]

    // Reset board whenever the challenge changes
    LaunchedEffect(challengeIdx) {
        vm.reset()
        feedback = null
    }

    fun check() {
        totalAttempts++
        feedback = if (boardState.currentValue == target) {
            score++
            "🎉 Correct! ${NumberFormatter.formatWithCommas(target)} — well done!"
        } else {
            "❌ You showed ${NumberFormatter.formatWithCommas(boardState.currentValue)}. " +
            "Target is ${NumberFormatter.formatWithCommas(target)}. Try again!"
        }
    }

    fun hint() {
        vm.setValueOnBoard(target)
        feedback = "💡 Hint: This is how ${NumberFormatter.formatWithCommas(target)} looks!"
    }

    fun next() {
        challengeIdx++
        feedback = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ── Challenge card ────────────────────────────────────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth(),
            colors    = CardDefaults.cardColors(containerColor = WoodBrown),
            shape     = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier            = Modifier.padding(18.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text  = "Show this number on the abacus:",
                    color = TextLight.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                AnimatedContent(
                    targetState = target,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "challenge"
                ) { t ->
                    Text(
                        text       = NumberFormatter.formatWithCommas(t),
                        color      = HeavenBead,
                        fontSize   = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign  = TextAlign.Center
                    )
                }
                Text(
                    text      = NumberFormatter.toWords(target),
                    color     = TextLight,
                    style     = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ── Abacus board ──────────────────────────────────────────────────────
        AbacusBoard(
            rods           = boardState.rods,
            onHeavenToggle = vm::toggleHeavenBead,
            onEarthToggle  = vm::toggleEarthBead
        )

        // ── Your value readout ────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text       = "Your value: ",
                style      = MaterialTheme.typography.titleMedium,
                color      = TextDark
            )
            Text(
                text       = NumberFormatter.formatWithCommas(boardState.currentValue),
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color      = if (boardState.currentValue == target) AccentGreen else TextDark
            )
        }

        // ── Feedback card ─────────────────────────────────────────────────────
        feedback?.let { msg ->
            val isCorrect = msg.startsWith("🎉")
            val isHint    = msg.startsWith("💡")
            val bgColor   = when {
                isCorrect -> Color(0xFFE8F5E9)
                isHint    -> Color(0xFFFFFDE7)
                else      -> Color(0xFFFFEBEE)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .padding(12.dp)
            ) {
                Text(
                    text  = msg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDark
                )
            }
        }

        // ── Action buttons ────────────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick  = ::check,
                modifier = Modifier.weight(1f),
                colors   = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Text("✓ Check", color = Color.White, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick  = ::hint,
                modifier = Modifier.weight(1f)
            ) {
                Text("💡 Hint")
            }

            if (feedback?.startsWith("🎉") == true || feedback?.startsWith("💡") == true) {
                Button(
                    onClick  = ::next,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(containerColor = WoodBrown)
                ) {
                    Text("Next →", color = TextLight)
                }
            }
        }

        // ── Progress bar ──────────────────────────────────────────────────────
        val progress = (challengeIdx % CHALLENGES.size + 1).toFloat() / CHALLENGES.size
        Column {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color    = HeavenBead,
                trackColor = WoodBrown.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text      = "Challenge ${challengeIdx % CHALLENGES.size + 1} / ${CHALLENGES.size}" +
                            "   •   Score: $score / $totalAttempts",
                style     = MaterialTheme.typography.labelSmall,
                color     = Color.Gray,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}
