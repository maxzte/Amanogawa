plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.model"
}

dependencies {
    implementation(projects.core.domain)
}
