import com.android.build.gradle.LibraryExtension
import id.anison.amanogawa.convention.Const
import id.anison.amanogawa.convention.configureKotlinAndroid
import id.anison.amanogawa.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.library")
        apply(plugin = "org.jetbrains.kotlin.android")

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = Const.TARGET_SDK
        }

        dependencies {
            "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
            "testImplementation"(libs.findLibrary("kotlin.test").get())

            "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
        }
    }
}
