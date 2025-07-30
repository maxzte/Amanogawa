plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.data"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)


    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation (libs.androidx.paging.compose)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
}