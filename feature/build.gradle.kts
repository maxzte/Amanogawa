plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "id.anison.feature"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {

    }
}

dependencies {
    api(projects.data)
    implementation(projects.common)

    implementation(libs.androidx.core.ktx)
    //COROUTINES
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    //TEST
    testImplementation(libs.junit.api)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //COMPOSE
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
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
    //COIL
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    //WORK MANAGER
    implementation(libs.androidx.work.runtime.ktx)
    //FONTS
    implementation(libs.androidx.ui.text.google.fonts)
    //PALETTE
    implementation(libs.androidx.palette.ktx)
}