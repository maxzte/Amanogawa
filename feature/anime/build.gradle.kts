plugins {
    alias(libs.plugins.amanogawa.feature)
}

android {
    namespace = "id.anison.amanogawa.feature.anime"
}

dependencies {
    implementation(projects.core.domain)

    //ZOOM
    implementation(libs.zoomable)
}
