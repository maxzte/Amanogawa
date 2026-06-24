plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.common"
}

dependencies {
    implementation(libs.kermit)
}
