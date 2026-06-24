plugins {
    alias(libs.plugins.amanogawa.feature)
}

android {
    namespace = "id.anison.amanogawa.feature.search"
}

dependencies {
    implementation(projects.core.domain)

    //PAGING
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.common)
}
