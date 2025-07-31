plugins {
    alias(libs.plugins.amanogawa.android.app)
    alias(libs.plugins.amanogawa.android.app.compose)
    alias(libs.plugins.amanogawa.koin.app)
}

android {
    namespace = "id.anison.amanogawa"

    defaultConfig {
        applicationId = "id.anison.amanogawa"
        versionCode = 1
        versionName = "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile(file("proguard/"))
        }
    }
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.data)
    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.navigation)
    implementation(projects.core.ui)
    implementation(projects.core.notification)
    implementation(projects.core.work)
    implementation(projects.core.common)
    implementation(projects.core.commonAndroid)


    implementation(projects.feature.home)
    implementation(projects.feature.search)
    implementation(projects.feature.favorite)
    implementation(projects.feature.profile)
    implementation(projects.feature.anime)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //COMPOSE
    implementation(libs.androidx.compose.material3)
    implementation(libs.material3.adaptive.navigation.suite)

    //NAVIGATION
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.tabNavigator)
    implementation(libs.voyager.transitions)
    implementation(libs.voyager.koin)

    //WORK_MANAGER
    implementation(libs.koin.workmanager)

    //SPLASHSCREEN
    implementation(libs.androidx.core.splashscreen)

    // COIL
    implementation(libs.coil)
}