pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            name = "spigotmc-repo"
        }
        maven("https://repo.helpch.at/releases") {
            name = "helpchat-repo"
        }
        maven("https://maven.enginehub.org/repo/") {
            name = "enginehub-repo"
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

rootProject.name = "SimpleScore"

include("api")
include("core")
include("bukkit")
include("bukkit:worldguard")
include("bukkit:worldguard:common")
include("bukkit:worldguard:v6")
include("bukkit:worldguard:v7")
