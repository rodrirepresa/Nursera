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
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Nursera"
include(":app")
include(":core:common")
include(":core:ui")
include(":feature:hospital:data")
include(":feature:hospital:domain")
include(":feature:hospital:presentation")
include(":feature:schedule:data")
include(":feature:schedule:domain")
include(":feature:schedule:presentation")
include(":feature:earnings:data")
include(":feature:earnings:domain")
include(":feature:earnings:presentation")
