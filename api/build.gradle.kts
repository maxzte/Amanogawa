plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jetbrains.kotlinx.coroutines.core)


    testImplementation(libs.junit.api)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
    testImplementation(libs.testng)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.mockk)
}