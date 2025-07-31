plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.datastore"
}

dependencies {
    implementation(projects.core.common)
    
    implementation(libs.coroutines.core)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
}