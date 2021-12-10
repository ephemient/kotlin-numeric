enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }

    plugins {
        resolutionStrategy {
            eachPlugin {
                if (requested.id.id.startsWith("org.jetbrains.kotlin.")) useVersion("1.5.31")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google {
            mavenContent {
                includeGroup("com.android.tools")
            }
        }
        maven(url = "https://jitpack.io") {
            mavenContent {
                includeModule("com.github.jetbrains", "jetCheck")
            }
        }
    }
}

rootProject.name = "kotlin-numeric"
include("annotations", "agent", "agent-test")
