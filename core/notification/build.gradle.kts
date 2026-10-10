import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.koin)
}

android {
    namespace = "id.anison.amanogawa.core.notification"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)

    //WORK MANAGER
    implementation(libs.androidx.work.runtime.ktx)

    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}

tasks.withType<Test> {
    useJUnitPlatform()
}