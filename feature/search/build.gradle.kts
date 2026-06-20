plugins {
    alias(libs.plugins.amanogawa.feature)
}

android {
    namespace = "id.anison.amanogawa.feature.search"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.activity.compose)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.material3.adaptive.navigation.suite)

    //PAGING
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.common)
}
