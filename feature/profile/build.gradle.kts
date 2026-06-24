plugins {
    alias(libs.plugins.amanogawa.feature)
}

android {
    namespace = "id.anison.amanogawa.feature.profile"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.aboutlibraries.compose.m3)
}
