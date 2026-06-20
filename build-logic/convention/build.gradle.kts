import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "id.anison.amanogawa.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.koin.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("app") {
            id = libs.plugins.amanogawa.app.get().pluginId
            implementationClass = "id.anison.amanogawa.convention.AppConventionPlugin"
        }
        register("androidLibCompose") {
            id = libs.plugins.amanogawa.android.lib.compose.get().pluginId
            implementationClass = "id.anison.amanogawa.convention.AndroidLibComposeConventionPlugin"
        }
        register("androidLib") {
            id = libs.plugins.amanogawa.android.lib.asProvider().get().pluginId
            implementationClass = "id.anison.amanogawa.convention.AndroidLibConventionPlugin"
        }
        register("feature") {
            id = libs.plugins.amanogawa.feature.get().pluginId
            implementationClass = "id.anison.amanogawa.convention.FeatureConventionPlugin"
        }
        register("koin") {
            id = libs.plugins.amanogawa.koin.get().pluginId
            implementationClass = "id.anison.amanogawa.convention.KoinConventionPlugin"
        }
        register("room") {
            id = libs.plugins.amanogawa.room.get().pluginId
            implementationClass = "id.anison.amanogawa.convention.RoomConventionPlugin"
        }
        register("kotlinLib") {
            id = libs.plugins.amanogawa.kotlin.lib.get().pluginId
            implementationClass = "id.anison.amanogawa.convention.KotlinLibConventionPlugin"
        }
    }
}
