import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.koin)
}

android {
    namespace = "id.anison.amanogawa.core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.network)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.domain)

    implementation(libs.coroutines.android)
    implementation(libs.androidx.paging.common)

    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
