plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.koin)
}

android {
    namespace = "id.anison.amanogawa.core.notification"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)

    //WORK MANAGER
    implementation(libs.androidx.work.runtime.ktx)
}