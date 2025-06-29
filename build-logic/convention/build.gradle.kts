plugins {
    `kotlin-dsl`
}

group = "id.anison.amanogawa.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidAppCompose") {
            id = libs.plugins.amanogawa.android.app.compose.get().pluginId
            implementationClass = "AndroidAppComposeConventionPlugin"
        }
        register("androidApp") {
            id = libs.plugins.amanogawa.android.app.asProvider().get().pluginId
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibCompose") {
            id = libs.plugins.amanogawa.android.lib.compose.get().pluginId
            implementationClass = "AndroidLibComposeConventionPlugin"
        }
        register("androidLib") {
            id = libs.plugins.amanogawa.android.lib.asProvider().get().pluginId
            implementationClass = "AndroidLibConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.amanogawa.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("koin") {
            id = libs.plugins.amanogawa.koin.get().pluginId
            implementationClass = "KoinConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.amanogawa.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("kotlinLib") {
            id = libs.plugins.amanogawa.kotlin.lib.get().pluginId
            implementationClass = "KotlinLibConventionPlugin"
        }
    }
}
