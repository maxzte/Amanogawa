plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies{
    implementation(libs.okhttp)
//    implementation(libs.okhttp.coroutines)
//    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.kotlinx.serialization.json)
}