plugins {
    alias(libs.plugins.amanogawa.android.lib)
    alias(libs.plugins.amanogawa.android.lib.compose)
}

android {
    namespace = "id.anison.amanogawa.core.ui"
}

dependencies {
    api(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)


    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    //KOIN
//    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    //Compose
    implementation(libs.androidx.compose.material3)
    implementation(libs.material3.adaptive.navigation.suite)

    implementation(libs.google.fonts)

    implementation(libs.palette)
}