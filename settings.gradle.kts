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

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://maven.enginehub.org/repo/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

rootProject.name = "SimpleScore"

include("api")
include("core")
include("bukkit")
include("bukkit:protocol")
include("bukkit:protocol:common")
include("bukkit:protocol:modern")
include("bukkit:protocol:legacy")
include("bukkit:worldguard")
include("bukkit:worldguard:api")
include("bukkit:worldguard:v6")
include("bukkit:worldguard:v7")