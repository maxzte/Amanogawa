plugins {
    alias(libs.plugins.amanogawa.android.lib.compose)
    alias(libs.plugins.amanogawa.android.feature)
}

android {
    namespace = "id.anison.amanogawa.feature.anime"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.navigation)
    implementation(projects.core.common)

    implementation(libs.androidx.core.ktx)

    //COROUTINES
    implementation(libs.coroutines.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //COMPOSE
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material3.adaptive.navigation.suite)

    //LIFECYCLE
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //NAVIGATION
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.tabNavigator)
    implementation(libs.voyager.transitions)
    implementation(libs.voyager.screenModel)
    implementation(libs.voyager.koin)

    //PAGING
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.common)

    //KOIN
    runtimeOnly(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    //WORK MANAGER
    implementation(libs.androidx.work.runtime.ktx)

    //ZOOM
    implementation(libs.zoomable)
}