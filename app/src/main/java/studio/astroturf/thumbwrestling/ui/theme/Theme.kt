package studio.astroturf.thumbwrestling.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        primary = DarkPrimary,
        secondary = DarkSecondary,
        background = DarkBackground,
        surface = DarkSurface,
        error = DarkError,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = LightPrimary,
        secondary = LightSecondary,
        background = LightBackground,
        surface = LightSurface,
        error = LightError,
    )

@Composable
fun ThumbWrestlingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
