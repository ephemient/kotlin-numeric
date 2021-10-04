enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io") {
            mavenContent {
                includeModule("com.github.jetbrains", "jetCheck")
            }
        }
    }
}

rootProject.name = "kotlin-numeric"
