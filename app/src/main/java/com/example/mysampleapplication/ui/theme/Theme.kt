package com.example.mysampleapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Full Material 3 light colour scheme.
 *
 * Primary role   → deep blue-indigo ([Blue800])
 * Secondary role → vibrant teal ([Teal600])
 * Surface/background → warm off-white ([Grey50] / [White])
 */
private val LightColorScheme = lightColorScheme(
    primary              = Blue800,
    onPrimary            = White,
    primaryContainer     = Blue100,
    onPrimaryContainer   = Blue900,
    secondary            = Teal600,
    onSecondary          = White,
    secondaryContainer   = Teal100,
    onSecondaryContainer = Teal700,
    tertiary             = Blue700,
    onTertiary           = White,
    tertiaryContainer    = Blue100,
    onTertiaryContainer  = Blue900,
    background           = Grey50,
    onBackground         = Grey900,
    surface              = White,
    onSurface            = Grey900,
    surfaceVariant       = BlueGrey50,
    onSurfaceVariant     = Grey800,
    error                = Red700,
    onError              = White,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),
    outline              = Grey800,
    outlineVariant       = Color(0xFFCAC4D0),
    scrim                = Black,
)

/**
 * Full Material 3 dark colour scheme.
 *
 * Primary role   → light blue-indigo ([Blue200])
 * Secondary role → light teal ([Teal200])
 * Surface/background → near-black ([Grey900])
 */
private val DarkColorScheme = darkColorScheme(
    primary              = Blue200,
    onPrimary            = Blue900,
    primaryContainer     = Blue800,
    onPrimaryContainer   = Blue100,
    secondary            = Teal200,
    onSecondary          = Teal700,
    secondaryContainer   = Teal700,
    onSecondaryContainer = Teal100,
    tertiary             = Blue200,
    onTertiary           = Blue900,
    tertiaryContainer    = Blue800,
    onTertiaryContainer  = Blue100,
    background           = Grey900,
    onBackground         = Grey100,
    surface              = Grey900,
    onSurface            = Grey100,
    surfaceVariant       = BlueGrey800,
    onSurfaceVariant     = Grey100,
    error                = Red200,
    onError              = Black,
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),
    outline              = Grey100,
    outlineVariant       = Color(0xFF49454F),
    scrim                = Black,
)

/**
 * Root Material 3 theme for MySampleApplication.
 *
 * Always applies [LightColorScheme] or [DarkColorScheme] for consistent branding —
 * dynamic colour (wallpaper-derived) is permanently disabled so the deep blue-indigo
 * and teal palette are preserved on all devices and API levels.
 *
 * @param darkTheme Whether to apply the dark colour scheme. Defaults to the system setting.
 * @param content   The composable content to theme.
 */
@Composable
fun MySampleApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content,
    )
}
