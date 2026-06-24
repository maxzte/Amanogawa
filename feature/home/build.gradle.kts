plugins {
    alias(libs.plugins.amanogawa.feature)
}

android {
    namespace = "id.anison.amanogawa.feature.home"
}

dependencies {
    implementation(projects.core.domain)
}
