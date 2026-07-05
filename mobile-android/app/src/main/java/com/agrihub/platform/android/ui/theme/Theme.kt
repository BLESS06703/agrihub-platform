package com.agrihub.platform.android.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = TextOnPrimary,
    primaryContainer = EmeraldGreen,
    secondary = HarvestGold,
    background = OffWhite,
    surface = SurfaceWhite,
    onBackground = Charcoal,
    onSurface = Charcoal,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkBackground,
    primaryContainer = ForestGreen,
    secondary = DarkAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText,
    error = ErrorRed
)

@Composable
fun AgriHubTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content
    )
}
