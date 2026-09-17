package com.abacus.app.ui.abacus

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abacus.app.model.RodState
import com.abacus.app.ui.theme.*

private const val ROD_WIDTH_DP  = 27   // Width of each rod column in dp
private const val BOARD_HEIGHT  = 290  // Total board height in dp
private val DIVIDER_HEIGHT      = 8.dp

/**
 * The visual abacus board: a wooden frame containing all 13 rods arranged
 * from the highest place value (trillions, left) to units (right).
 *
 * A horizontal divider bar overlays the board at ~38% from the top,
 * separating the heaven section from the earth section on every rod.
 *
 * Place-value labels appear above and per-rod digit values below.
 */
@Composable
fun AbacusBoard(
    rods:           List<RodState>,
    onHeavenToggle: (rodIndex: Int) -> Unit,
    onEarthToggle:  (rodIndex: Int, displayBeadIndex: Int) -> Unit,
    modifier:       Modifier = Modifier
) {
    val displayRods = rods.reversed()   // left = highest place value

    Column(modifier = modifier.fillMaxWidth()) {

        // ── Place-value labels ────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        ) {
            displayRods.forEach { rod ->
                Text(
                    text      = placeLabel(rod.index),
                    fontSize  = 9.sp,
                    color     = WoodBrown,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.width(ROD_WIDTH_DP.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Wooden board frame ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(BOARD_HEIGHT.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(WoodBrown)
                .padding(6.dp)
        ) {
            // Inner board surface (lighter wood grain)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WoodLight.copy(alpha = 0.55f))
            ) {
                // ── All rods ──────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    displayRods.forEach { rod ->
                        AbacusRod(
                            rod            = rod,
                            onHeavenToggle = { onHeavenToggle(rod.index) },
                            onEarthToggle  = { i -> onEarthToggle(rod.index, i) },
                            modifier       = Modifier
                                .width(ROD_WIDTH_DP.dp)
                                .fillMaxHeight()
                        )
                    }
                }

                // ── Horizontal divider bar overlay ────────────────────────────
                // Sits at the boundary between heaven (38%) and earth (62%) sections.
                // The 8 dp matches the Spacer in AbacusRod.
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val dividerTop = (maxHeight - DIVIDER_HEIGHT) * 0.38f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(DIVIDER_HEIGHT)
                            .offset(y = dividerTop)
                            .background(WoodDark)
                    ) {
                        // Top highlight edge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x33FFC864))
                        )
                        // Inlaid mother-of-pearl alignment dots (Hoshi markers)
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            displayRods.forEach { rod ->
                                Box(
                                    modifier = Modifier.width(ROD_WIDTH_DP.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (rod.index == 0 || rod.index == 3 || rod.index == 6 || rod.index == 9 || rod.index == 12) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.5.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(Color(0xFFFFF8EE))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Per-rod digit value readout ───────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
        ) {
            displayRods.forEach { rod ->
                val v = rod.value
                Text(
                    text      = if (v > 0) v.toString() else "·",
                    fontSize  = 11.sp,
                    color     = if (v > 0) WoodBrown else WoodBrown.copy(alpha = 0.3f),
                    fontWeight = if (v > 0) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.width(ROD_WIDTH_DP.dp)
                )
            }
        }
    }
}

/** Short place-value label for rod [index]. */
private fun placeLabel(index: Int): String = when (index) {
    0  -> "U"
    1  -> "T"
    2  -> "H"
    3  -> "K"
    4  -> "TK"
    5  -> "HK"
    6  -> "M"
    7  -> "TM"
    8  -> "HM"
    9  -> "B"
    10 -> "TB"
    11 -> "HB"
    12 -> "T"
    else -> "?"
}
