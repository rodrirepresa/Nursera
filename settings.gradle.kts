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
include(":feature:home:data")
include(":feature:home:domain")
include(":feature:home:presentation")
include(":feature:favorites:data")
include(":feature:favorites:domain")
include(":feature:favorites:presentation")
include(":feature:profile:data")
include(":feature:profile:domain")
include(":feature:profile:presentation")
