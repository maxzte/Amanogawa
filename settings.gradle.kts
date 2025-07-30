enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Amanogawa"
include(":app")
include(":core:network")
include(":core:database")
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:model")
include(":core:ui")
include(":core:notification")
include(":core:navigation")
include(":core:work")

include(":feature:home")
include(":feature:search")
include(":feature:favorite")
include(":feature:profile")
include(":feature:anime")
