plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies{
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
}