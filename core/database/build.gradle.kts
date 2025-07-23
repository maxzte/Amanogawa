plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.android.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "id.anison.amanogawa.core.database"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}