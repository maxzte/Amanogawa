import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.koin)
}

android {
    namespace = "id.anison.amanogawa.core.work"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.notification)

    //KOIN
    implementation(libs.koin.workmanager)

    //WORK MANAGER
    implementation(libs.androidx.work.runtime.ktx)

    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
