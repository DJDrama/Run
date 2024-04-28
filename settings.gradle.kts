pluginManagement {
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

// Access modules in type-safe manner
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Runrun"
include(":app")
include(":auth:data")
include(":auth:presentation")
include(":core:presentation:designsystem")
include(":core:presentation:ui")
include(":core:domain")
include(":auth:domain")
include(":core:data")
include(":core:database")
include(":run:data")
include(":run:presentation")
include(":run:domain")
include(":run:location")
include(":run:network")
