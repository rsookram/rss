package io.github.rsookram.rss.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Material 2 theme which uses monochrome colours.
 */
@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val primary = Color(if (darkTheme) 0xFFFFFFFF else 0xFF121212)
    val primaryVariant = Color(if (darkTheme) 0xFFE0E0E0 else 0xFF000000)
    val secondary = Color(if (darkTheme) 0xFF757575 else 0xFFE0E0E0)
    val background = Color(if (darkTheme) 0xFF121212 else 0xFFFFFFFF)
    val error = Color(if (darkTheme) 0xFFCF6679 else 0xFFB00020)
    val onPrimary = if (darkTheme) Color.Black else Color.White
    val onSecondary = if (darkTheme) Color.Black else Color.Black
    val onBackground = if (darkTheme) Color.White else Color.Black
    val onError = if (darkTheme) Color.Black else Color.White

    MaterialTheme(
        Colors(
            primary,
            primaryVariant,
            secondary,
            secondaryVariant = secondary,
            background,
            surface = background,
            error,
            onPrimary,
            onSecondary,
            onBackground,
            onSurface = onBackground,
            onError,
            isLight = !darkTheme,
        ),
        content = content,
    )
}

@Preview
@Composable
private fun ThemePreviewLight() {
    AppTheme {
        ThemePreview()
    }
}

@Preview
@Composable
private fun ThemePreviewDark() {
    AppTheme(darkTheme = true) {
        ThemePreview()
    }
}

@Composable
private fun ThemePreview() {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            ColourPreview(
                Modifier.weight(1f),
                name = "Primary",
                colour = MaterialTheme.colors.primary,
                textColour = MaterialTheme.colors.onPrimary,
            )

            ColourPreview(
                Modifier.weight(1f),
                name = "Primary Variant",
                colour = MaterialTheme.colors.primaryVariant,
                textColour = MaterialTheme.colors.onPrimary,
            )

            ColourPreview(
                Modifier.weight(1f),
                name = "Secondary",
                colour = MaterialTheme.colors.secondary,
                textColour = MaterialTheme.colors.onSecondary,
            )

            ColourPreview(
                Modifier.weight(1f),
                name = "Secondary Variant",
                colour = MaterialTheme.colors.secondaryVariant,
                textColour = MaterialTheme.colors.onSecondary,
            )
        }

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            ColourPreview(
                Modifier.weight(1f),
                name = "Background",
                colour = MaterialTheme.colors.background,
                textColour = MaterialTheme.colors.onBackground,
            )

            ColourPreview(
                Modifier.weight(1f),
                name = "Surface",
                colour = MaterialTheme.colors.surface,
                textColour = MaterialTheme.colors.onSurface,
            )

            ColourPreview(
                Modifier.weight(1f),
                name = "Error",
                colour = MaterialTheme.colors.error,
                textColour = MaterialTheme.colors.onError,
            )
        }

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            ColourPreview(
                Modifier.weight(1f),
                name = "On Primary",
                colour = MaterialTheme.colors.onPrimary,
                textColour = MaterialTheme.colors.primary,
            )

            ColourPreview(
                Modifier.weight(1f),
                name = "On Secondary",
                colour = MaterialTheme.colors.onSecondary,
                textColour = MaterialTheme.colors.secondary,
            )
        }

        Spacer(Modifier.height(1.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            ColourPreview(
                Modifier.weight(1f),
                name = "On Background",
                colour = MaterialTheme.colors.onBackground,
                textColour = MaterialTheme.colors.background,
            )

            ColourPreview(
                Modifier.weight(1f),
                name = "On Surface",
                colour = MaterialTheme.colors.onSurface,
                textColour = MaterialTheme.colors.surface,
            )

            ColourPreview(
                Modifier.weight(1f),
                name = "On Error",
                colour = MaterialTheme.colors.onError,
                textColour = MaterialTheme.colors.error,
            )
        }
    }
}

@Composable
private fun ColourPreview(
    modifier: Modifier = Modifier,
    name: String,
    colour: Color,
    textColour: Color,
) {
    Box(
        modifier
            .background(colour)
            .padding(8.dp)
            .heightIn(min = 48.dp)
    ) {
        Text(name, color = textColour)
    }
}
