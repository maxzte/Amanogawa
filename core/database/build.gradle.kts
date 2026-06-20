plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.amanogawa.koin)
}

android {
    namespace = "id.anison.amanogawa.core.database"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
