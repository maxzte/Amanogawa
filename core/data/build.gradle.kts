plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.koin)
}

android {
    namespace = "id.anison.amanogawa.core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.network)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.domain)


    implementation(libs.coroutines.android)

    implementation (libs.androidx.paging.compose)

}
