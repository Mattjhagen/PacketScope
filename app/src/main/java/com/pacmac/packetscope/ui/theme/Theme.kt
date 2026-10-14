package com.pacmac.packetscope.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Background,
    primaryContainer = CyanAccent,
    onPrimaryContainer = TextPrimary,

    secondary = PurpleAccent,
    onSecondary = Background,
    secondaryContainer = PurpleAccent,
    onSecondaryContainer = TextPrimary,

    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = BottomNav,
    onSurfaceVariant = TextSecondary,

    error = Danger,
    onError = TextPrimary
)

@Composable
fun PacketScopeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
