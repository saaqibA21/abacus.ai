package com.abacus.app.ui.abacus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.abacus.app.model.AbacusState
import com.abacus.app.model.RodState
import com.abacus.app.ui.theme.RodColor

/**
 * Renders a single vertical abacus rod with its heaven bead (above the bar) and
 * four earth beads (below the bar).
 *
 * Layout (top → bottom):
 *   ┌────────────────────────────┐
 *   │  heaven section  (38%)     │  — heaven bead floats top (inactive) or bottom (active)
 *   │  ── [8 dp spacer] ──       │  — aligns with the divider bar drawn by AbacusBoard
 *   │  earth section   (62%)     │  — active earth beads cluster at top, inactive at bottom
 *   └────────────────────────────┘
 *
 * @param onHeavenToggle         Called when the heaven bead is tapped.
 * @param onEarthToggle(i)       Called when earth bead at display index i is tapped.
 *                               i=0 is closest to the bar, i=3 is farthest.
 */
@Composable
fun AbacusRod(
    rod:            RodState,
    onHeavenToggle: () -> Unit,
    onEarthToggle:  (Int) -> Unit,
    modifier:       Modifier = Modifier
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Heaven section ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.38f)
        ) {
            // Rod line
            RodLine(Modifier.align(Alignment.Center))

            // Heaven bead: inactive → near top, active → near divider bar
            HeavenBeadComposable(
                isActive = rod.heavenActive,
                onClick  = onHeavenToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .align(
                        if (rod.heavenActive) Alignment.BottomCenter
                        else                  Alignment.TopCenter
                    )
            )
        }

        // Spacer that lines up with the divider bar overlay in AbacusBoard
        Spacer(modifier = Modifier.height(8.dp))

        // ── Earth section ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.62f)
        ) {
            // Rod line
            RodLine(Modifier.align(Alignment.Center))

            // Active earth beads: cluster at the TOP (near bar)
            Column(
                modifier            = Modifier.align(Alignment.TopCenter).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                for (i in 0 until rod.earthCount) {
                    EarthBeadComposable(
                        isActive = true,
                        onClick  = { onEarthToggle(i) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    )
                }
            }

            // Inactive earth beads: cluster at the BOTTOM (away from bar)
            Column(
                modifier            = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                for (i in rod.earthCount until AbacusState.EARTH_BEADS) {
                    EarthBeadComposable(
                        isActive = false,
                        onClick  = { onEarthToggle(i) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RodLine(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(4.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(2.dp))
            .background(RodColor)
    )
}
