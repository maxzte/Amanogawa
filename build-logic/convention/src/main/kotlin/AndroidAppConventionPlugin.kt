import com.android.build.api.dsl.ApplicationExtension
import id.anison.amanogawa.convention.Const
import id.anison.amanogawa.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.application")
        apply(plugin = "org.jetbrains.kotlin.android")

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = Const.TARGET_SDK
        }
    }
}
