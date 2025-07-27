pluginManagement {
    repositories {
        gradlePluginPortal()
        google() // Ajoutez ce dépôt
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ProjetTDM"
include(":app")
