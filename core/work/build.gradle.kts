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
}
