package id.anison.amanogawa.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureKoinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        dependencies {
            val bom = libs.findLibrary("koin.bom").get()
            add("implementation", platform(bom))
            add("implementation", libs.findLibrary("koin.android").get())
            add("implementation", libs.findLibrary("koin.androidx.compose").get())
            add("implementation", libs.findLibrary("koin.workmanager").get())
            add("compileOnly", libs.findLibrary("koin.annotations.core").get())
            add("ksp", libs.findLibrary("koin.annotations.compiler").get())
        }
    }
}
