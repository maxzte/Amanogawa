plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.domain"
}

dependencies {
    api(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.coroutines.core)
    implementation(libs.androidx.paging.common)
}