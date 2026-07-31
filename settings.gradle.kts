@file:Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    // configures repositories for all projects
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.dmulloy2.net/nexus/repository/public/")
    }

    // only use these repos
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "CPSChecker"

include(":core")
include(":plugin")

include(":protocol-v1_8_8")
include("protocol-v1_9")
