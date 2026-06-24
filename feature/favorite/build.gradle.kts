plugins {
    alias(libs.plugins.amanogawa.feature)
}

android {
    namespace = "id.anison.amanogawa.feature.favorite"
}

dependencies {
    implementation(projects.core.domain)
}
