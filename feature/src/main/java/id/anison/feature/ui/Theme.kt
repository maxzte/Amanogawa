package id.anison.feature.ui

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

private val blackColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    background = Color.Black,
    onBackground = onBackgroundDark,
    surface = Color.Black,
    onSurface = onSurfaceDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@RequiresApi(Build.VERSION_CODES.S)
fun dynamicBlackColorScheme(context: Context): ColorScheme {
    val dynamicDarkColors = dynamicDarkColorScheme(context)

    return dynamicDarkColors.copy(
        primary = dynamicDarkColors.primary,
        onPrimary = dynamicDarkColors.onPrimary,
        secondary = dynamicDarkColors.secondary,
        onSecondary = dynamicDarkColors.onSecondary,
        background = Color.Black,
        onBackground = dynamicDarkColors.onBackground,
        surface = Color.Black,
        onSurface = dynamicDarkColors.onSurface,
        primaryContainer = dynamicDarkColors.primaryContainer,
        onPrimaryContainer = dynamicDarkColors.onPrimaryContainer,
        secondaryContainer = dynamicDarkColors.secondaryContainer,
        onSecondaryContainer = dynamicDarkColors.onSecondaryContainer,
        tertiary = dynamicDarkColors.tertiary,
        onTertiary = dynamicDarkColors.onTertiary,
        tertiaryContainer = dynamicDarkColors.tertiaryContainer,
        onTertiaryContainer = dynamicDarkColors.onTertiaryContainer,
        error = dynamicDarkColors.error,
        onError = dynamicDarkColors.onError,
        errorContainer = dynamicDarkColors.errorContainer,
        onErrorContainer = dynamicDarkColors.onErrorContainer,
        surfaceVariant = dynamicDarkColors.surfaceVariant,
        onSurfaceVariant = dynamicDarkColors.onSurfaceVariant,
        outline = dynamicDarkColors.outline,
        outlineVariant = dynamicDarkColors.outlineVariant,
        scrim = dynamicDarkColors.scrim,
        inverseSurface = dynamicDarkColors.inverseSurface,
        inverseOnSurface = dynamicDarkColors.inverseOnSurface,
        inversePrimary = dynamicDarkColors.inversePrimary,
        surfaceDim = dynamicDarkColors.surfaceDim,
        surfaceBright = dynamicDarkColors.surfaceBright,
        surfaceContainerLowest = dynamicDarkColors.surfaceContainerLowest,
        surfaceContainerLow = dynamicDarkColors.surfaceContainerLow,
        surfaceContainer = dynamicDarkColors.surfaceContainer,
        surfaceContainerHigh = dynamicDarkColors.surfaceContainerHigh,
        surfaceContainerHighest = dynamicDarkColors.surfaceContainerHighest,
    )
}

@Composable
fun AmanogawaTheme(
    darkTheme: Boolean = false,// isSystemInDarkTheme(),
    isBlackTheme: Boolean = false,
    dynamicColors: Boolean = false,
    content: @Composable () -> Unit
) {
    Log.d(
        "ATheme",
        "isBlackTheme: $isBlackTheme,\n darkTheme: $darkTheme,\n dynamicColors: $dynamicColors"
    )
    val context = LocalContext.current
    val isDynamicColorsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColors && isDynamicColorsSupported -> {
            when {
                // IF DARK + BLACK
                darkTheme && isBlackTheme -> dynamicBlackColorScheme(context)
                // IF LIGHT + BLACK
                !darkTheme && isBlackTheme -> dynamicLightColorScheme(context)
                // IF DARK
                darkTheme -> dynamicDarkColorScheme(context)
                // IF LIGHT
                else -> dynamicLightColorScheme(context)
            }
        }

        darkTheme && isBlackTheme -> blackColorScheme
        !darkTheme && isBlackTheme -> lightScheme
        darkTheme -> darkScheme
        else -> lightScheme
    }

    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        container = colorScheme.surface,
        bottom = colorScheme.primaryContainer,
    )
    val gradientColors = when {
        ((!dynamicColors || !isDynamicColorsSupported) || (dynamicColors && isDynamicColorsSupported && darkTheme && isBlackTheme)) ->
            GradientColors(
                top = Color.Unspecified,
                container = Color.Unspecified,
                bottom = Color.Unspecified
            )

        else -> defaultGradientColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        val insetsController = WindowCompat.getInsetsController(window, view)
        val configuration = LocalConfiguration.current
        SideEffect {
            /*window.statusBarColor = if (!darkTheme) colorScheme.surfaceContainerLowest.toArgb()
            else Color.Transparent.toArgb()
            window.navigationBarColor = if (!darkTheme) colorScheme.surfaceContainerLowest.toArgb()
            else Color.Transparent.toArgb()*/

            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = false
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                insetsController.apply {
                    insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }

//            window.navigationBarColor = Color.Transparent.toArgb()
        }
    }
    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,//.switch(),
            typography = AppTypography,
            content = content
        )
    }
}


@Composable
private fun animateColor(targetValue: Color) =
    animateColorAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 250), label = ""
    ).value

@Composable
fun ColorScheme.switch() = copy(
    primary = animateColor(targetValue = primary),
    //onPrimary = animateColor(targetValue = onPrimary),
    //secondary = animateColor (targetValue = secondary),
    //onSecondary = animateColor(targetValue = onSecondary),
    background = animateColor(targetValue = background),
    onBackground = animateColor(targetValue = onBackground),
    surface = animateColor(targetValue = surface),
    onSurface = animateColor(targetValue = onSurface),
    //error = animateColor(targetValue = error),
    //onError = animateColor(targetValue = onError),
    //surfaceContainer = animateColor(targetValue = surfaceContainer),
    //primaryContainer = animateColor(targetValue = primaryContainer),
    //onPrimaryContainer = animateColor(targetValue = onPrimaryContainer),
    //secondaryContainer = animateColor(targetValue = secondaryContainer),
    //onSecondaryContainer = animateColor(targetValue = onSecondaryContainer),
    //tertiary = animateColor(targetValue = tertiary),
    //onTertiary = animateColor(targetValue = onTertiary),
    //tertiaryContainer = animateColor(targetValue = tertiaryContainer),
    //onTertiaryContainer = animateColor(targetValue = onTertiaryContainer),
    //errorContainer = animateColor(targetValue = errorContainer),
    //onErrorContainer = animateColor(targetValue = onErrorContainer),
    //surfaceVariant = animateColor(targetValue = surfaceVariant),
    //onSurfaceVariant = animateColor(targetValue = onSurfaceVariant),
    //outline = animateColor(targetValue = outline),
    //outlineVariant = animateColor(targetValue = outlineVariant),
    //scrim = animateColor(targetValue = scrim),
    //inverseSurface = animateColor(targetValue = inverseSurface),
    //inverseOnSurface = animateColor(targetValue = inverseOnSurface),
    //inversePrimary = animateColor(targetValue = inversePrimary),
    //surfaceDim = animateColor(targetValue = surfaceDim),
    //surfaceBright = animateColor(targetValue = surfaceBright),
    //surfaceContainerLowest = animateColor(targetValue = surfaceContainerLowest),
    //surfaceContainerLow = animateColor(targetValue = surfaceContainerLow),
    //surfaceContainerHigh = animateColor(targetValue = surfaceContainerHigh),
    //surfaceContainerHighest = animateColor(targetValue = surfaceContainerHighest),
)