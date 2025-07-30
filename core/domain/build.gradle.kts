plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)
}