plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.navigation"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.voyager.navigator)
}