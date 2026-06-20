package id.anison.amanogawa.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import id.anison.amanogawa.convention.util.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.koin.compiler.plugin.KoinGradleExtension

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.insert-koin.compiler.plugin")

            configure<KoinGradleExtension> {
                compileSafety.set(true)
                userLogs.set(false)
                debugLogs.set(false)
            }

            configureKoinDependencies()
        }
    }
}

private fun Project.configureKoinDependencies() {
    dependencies {
        val bom = libs.findLibrary("koin.bom").get()
        add("implementation", libs.findLibrary("koin.compilerPlugin").get())
        add("implementation", platform(bom))
        add("implementation", libs.findLibrary("koin.core").get())
        add("implementation", libs.findLibrary("koin.core.coroutines").get())
        add("implementation", libs.findLibrary("koin.annotations").get())

        extensions.findByType<LibraryExtension>()?.let {
            add("implementation", libs.findLibrary("koin.android").get())
        }
        extensions.findByType<ApplicationExtension>()?.let {
            add("implementation", libs.findLibrary("koin.android").get())
        }
    }
}
