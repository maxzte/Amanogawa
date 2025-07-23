plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.work"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.notification)

    //KOIN
    runtimeOnly(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    //WORK MANAGER
    implementation(libs.androidx.work.runtime.ktx)
}