
import id.anison.amanogawa.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class KotlinLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
            apply(plugin = "org.jetbrains.kotlin.jvm")

            dependencies {
                "testImplementation"(libs.findLibrary("kotlin.test").get())
            }
        }
}
