enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            version("kotlin", embeddedKotlinVersion)
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
