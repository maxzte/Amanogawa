package id.anison.amanogawa.convention

import id.anison.amanogawa.convention.util.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "amanogawa.android.lib.compose")
            apply(plugin = "amanogawa.koin")

            dependencies {
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:common"))
                "implementation"(project(":core:navigation"))
                "implementation"(project(":core:model"))

                "implementation"(libs.findLibrary("koin.androidx.compose").get())

                "implementation"(libs.findLibrary("voyager.navigator").get())
                "implementation"(libs.findLibrary("voyager.tabNavigator").get())
                "implementation"(libs.findLibrary("voyager.screenModel").get())
                "implementation"(libs.findLibrary("voyager.transitions").get())
                "implementation"(libs.findLibrary("voyager.koin").get())

                "implementation"(libs.findLibrary("androidx.lifecycle.runtime.ktx").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtime.compose").get())
                "implementation"(libs.findLibrary("androidx.activity.compose").get())
                "implementation"(libs.findLibrary("androidx.core.ktx").get())
                "implementation"(libs.findLibrary("coroutines.android").get())
            }
        }
    }
}
