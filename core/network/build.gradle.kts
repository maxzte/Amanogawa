plugins {
    alias(libs.plugins.amanogawa.kotlin.lib)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.amanogawa.koin)
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktorfit.lib)


    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
    testImplementation(libs.mockwebserver)
}

tasks.test {
    useJUnitPlatform()
}
