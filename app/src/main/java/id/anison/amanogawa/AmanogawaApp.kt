package id.anison.amanogawa

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.anison.feature.MainScreen
import id.anison.feature.ui.AmanogawaBackground
import id.anison.feature.ui.AmanogawaTheme

@Composable
fun AmanogawaApp() {
    MainScreen().Content()
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
annotation class ThemePreviews

@ThemePreviews
@Composable
fun GradientBackgroundDefault() {
    AmanogawaTheme(dynamicColors = true) {
        AmanogawaBackground(Modifier.size(100.dp), content = {})
    }
}

@ThemePreviews
@Composable
fun GradientBackgroundDynamic() {
    AmanogawaTheme(dynamicColors = false) {
        AmanogawaBackground(Modifier.size(100.dp), content = {})
    }
}