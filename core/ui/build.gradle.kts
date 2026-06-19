plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.android.lib.compose)
}

android {
    namespace = "id.anison.amanogawa.core.ui"
}

dependencies {
    implementation(projects.core.common)

    //KOIN
    implementation(libs.koin.androidx.compose)

    //COIL
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.icons.core)
    implementation(libs.androidx.compose.icons.extended)

    implementation(libs.google.fonts)

    implementation(libs.palette)

    implementation(libs.androidx.browser)
}