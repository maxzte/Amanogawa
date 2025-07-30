import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "amanogawa.android.lib")
//        apply(plugin = "amanogawa.koin")


        dependencies {
            "implementation"(project(":core:ui"))

        }
    }
}
