package com.abacus.app.ui.abacus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.abacus.app.ui.theme.*

/**
 * The single heaven bead for a rod.
 *
 * @param isActive  True = bead is slid DOWN toward the divider bar (counted).
 * @param onClick   Called when the user taps the bead.
 */
@Composable
fun HeavenBeadComposable(
    isActive: Boolean,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val gradient = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFF394),
                Color(0xFFFFBE1A),
                Color(0xFFBF7F00),
                Color(0xFF573300)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFEDB0),
                Color(0xFFFFD54F),
                Color(0xFFFFA000),
                Color(0xFF7A4500)
            )
        )
    }

    BeadShape(
        brush    = gradient,
        stroke   = if (isActive) Color(0xFF6B3E00) else Color(0xFF422200),
        isActive = isActive,
        modifier = modifier,
        onClick  = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    )
}

/**
 * One of the four earth beads on a rod.
 *
 * @param isActive  True = bead is slid UP toward the divider bar (counted).
 * @param onClick   Called when the user taps the bead.
 */
@Composable
fun EarthBeadComposable(
    isActive: Boolean,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val gradient = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFC085),
                Color(0xFFFF7A00),
                Color(0xFFB84500),
                Color(0xFF5C1C00)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFF0DC),
                Color(0xFFFFCC80),
                Color(0xFFE68A00),
                Color(0xFF663300)
            )
        )
    }

    BeadShape(
        brush    = gradient,
        stroke   = if (isActive) Color(0xFF5C1C00) else Color(0xFF381B00),
        isActive = isActive,
        modifier = modifier,
        onClick  = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    )
}

@Composable
private fun BeadShape(
    brush:    Brush,
    stroke:   Color,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {
    Box(
        modifier = modifier
            .height(20.dp)
            .shadow(
                elevation = if (isActive) 4.dp else 2.dp,
                shape = RoundedCornerShape(50)
            )
            .clip(RoundedCornerShape(50))
            .background(brush)
            .border(0.8.dp, stroke, RoundedCornerShape(50))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Upper specular reflection arc
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(4.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isActive) 0.65f else 0.45f),
                            Color.White.copy(alpha = 0f)
                        )
                    )
                )
        )

        // Center equatorial ridge highlight (gives the bicone appearance)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(1.dp)
                .background(Color.White.copy(alpha = if (isActive) 0.35f else 0.2f))
        )

        // Center borehole showing the rod
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 4.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2826))
        )
    }
}
