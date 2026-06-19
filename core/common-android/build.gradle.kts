plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.common"
}

dependencies {
    api(projects.core.common)
}