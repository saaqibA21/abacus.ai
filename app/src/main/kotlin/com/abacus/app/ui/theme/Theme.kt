package com.abacus.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AbacusColorScheme = lightColorScheme(
    primary            = WoodBrown,
    onPrimary          = TextLight,
    primaryContainer   = WoodLight,
    onPrimaryContainer = TextLight,
    secondary          = HeavenBead,
    onSecondary        = TextDark,
    tertiary           = AccentGreen,
    onTertiary         = TextLight,
    background         = CreamBackground,
    onBackground       = TextDark,
    surface            = CreamBackground,
    onSurface          = TextDark,
    error              = AccentRed,
    onError            = TextLight
)

@Composable
fun AbacusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AbacusColorScheme,
        typography  = AbacusTypography,
        content     = content
    )
}
