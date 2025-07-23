plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.notification"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)

    //KOIN
    runtimeOnly(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    //WORK MANAGER
    implementation(libs.androidx.work.runtime.ktx)
}