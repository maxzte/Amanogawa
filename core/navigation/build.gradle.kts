plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.android.lib.compose)
}

android {
    namespace = "id.anison.amanogawa.core.navigation"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.voyager.navigator)
}