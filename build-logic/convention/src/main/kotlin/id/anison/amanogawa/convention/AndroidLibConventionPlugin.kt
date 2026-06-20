package id.anison.amanogawa.convention

import com.android.build.api.dsl.LibraryExtension
import id.anison.amanogawa.convention.util.configureKotlinAndroid
import id.anison.amanogawa.convention.util.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }

            dependencies {
                "androidTestImplementation"(libs.findLibrary("androidx.espresso.core").get())
                "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
                "testImplementation"(libs.findLibrary("junit").get())
            }
        }
    }
}
