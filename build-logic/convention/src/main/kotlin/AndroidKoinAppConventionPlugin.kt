import com.android.build.api.dsl.ApplicationExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import id.anison.amanogawa.convention.configureKoinAndroid

class AndroidKoinAppConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                arg("KOIN_CONFIG_CHECK", "true")
            }

            val applicationExtension = extensions.getByType<ApplicationExtension>()
            configureKoinAndroid(applicationExtension)
        }
    }

}