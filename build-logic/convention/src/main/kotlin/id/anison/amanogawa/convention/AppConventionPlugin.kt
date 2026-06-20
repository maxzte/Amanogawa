package id.anison.amanogawa.convention

import com.android.build.api.dsl.ApplicationExtension
import id.anison.amanogawa.convention.util.Const
import id.anison.amanogawa.convention.util.configureAndroidCompose
import id.anison.amanogawa.convention.util.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            extensions.configure<ApplicationExtension> {
                configureAndroidCompose(this)
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = Const.TARGET_SDK
            }
        }
    }
}
