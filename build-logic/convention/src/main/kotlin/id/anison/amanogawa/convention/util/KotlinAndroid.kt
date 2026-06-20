package id.anison.amanogawa.convention.util

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project


internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        compileSdk = Const.TARGET_SDK

        defaultConfig.apply {
            minSdk = Const.MIN_SDK
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_25
            targetCompatibility = JavaVersion.VERSION_25
        }
    }
}

