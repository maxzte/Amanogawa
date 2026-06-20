plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.koin)
}

android {
    namespace = "id.anison.amanogawa.core.datastore"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    
    implementation(libs.coroutines.core)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
}