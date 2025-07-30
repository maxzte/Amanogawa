plugins {
    alias(libs.plugins.amanogawa.android.feature)
    alias(libs.plugins.amanogawa.android.lib.compose)
}

android {
    namespace = "id.anison.amanogawa.feature.search"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.navigation)
    implementation(projects.core.common)

    implementation(libs.androidx.core.ktx)
    //COROUTINES
    implementation(libs.kotlinx.coroutines.android)

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

    runtimeOnly(libs.koin.core)
    implementation(libs.koin.androidx.compose)


    //PAGING
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.common)
}