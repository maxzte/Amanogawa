import org.gradle.kotlin.dsl.libs

plugins {
    alias(libs.plugins.amanogawa.android.lib)
}

android {
    namespace = "id.anison.amanogawa.core.common"
}
