pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        val githubToken = providers.gradleProperty("gpr.key").orNull ?: System.getenv("TOKEN")
        if (!githubToken.isNullOrBlank()) {
            maven {
                url = uri("https://maven.pkg.github.com/hammerheadnav/karoo-ext")
                credentials {
                    username = providers.gradleProperty("gpr.user").getOrElse(System.getenv("USERNAME"))
                    password = githubToken
                }
            }
        }
    }
}

rootProject.name = "KarooBrake"
include("app")
