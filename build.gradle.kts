import io.papermc.hangarpublishplugin.model.Platforms
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("maven-publish")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
}

group = "com.r4g3baby"
version = "4.1.1-dev"

dependencies {
    api(project("bukkit"))
}

subprojects {
    group = "${rootProject.group}.${rootProject.name.lowercase()}"
    version = rootProject.version

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    java {
        withSourcesJar()
    }
}

allprojects {
    kotlin {
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(8)
        }
    }

    tasks {
        processResources {
            filteringCharset = "UTF-8"
            filesMatching(listOf("**plugin.yml")) {
                filter<ReplaceTokens>(
                    "tokens" to mapOf(
                        "name" to rootProject.name,
                        "version" to rootProject.version,
                        "description" to "A simple animated scoreboard plugin for your server.",
                        "package" to "${rootProject.group}.${rootProject.name.lowercase()}",
                        "website" to "https://ruipereira.dev"
                    )
                )
            }
        }
    }
}

tasks {
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")

        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        val libs = "${project.group}.${project.name.lowercase()}.lib"
        relocate("org.objenesis", "$libs.objenesis")
        relocate("net.swiftzer.semver", "$libs.semver")
        relocate("org.bstats", "$libs.bstats")
        // relocate("com.zaxxer.hikari", "$libs.hikari")
        // relocate("org.slf4j", "$libs.slf4j")

        relocate("org.jetbrains", "$libs.jetbrains")
        relocate("kotlin", "$libs.kotlin")

        from(file("LICENSE"))

        dependencies {
            exclude("META-INF/**")
        }

        minimize()
    }

    hangarPublish {
        publications.register("plugin") {
            apiKey = findProperty("hangar.token") as String? ?: System.getenv("HANGAR_TOKEN")
            id = property("hangar.project") as String?
            version = project.version as String
            channel = "Release"
            changelog = parseGitHubChangelog()

            platforms {
                register(Platforms.PAPER) {
                    jar.set(shadowJar.flatMap { it.archiveFile })
                    platformVersions = mapVersions("hangar.versions")
                }
            }
        }
    }

    modrinth {
        token = findProperty("modrinth.token") as String? ?: System.getenv("MODRINTH_TOKEN")
        projectId = property("modrinth.project") as String?
        uploadFile = shadowJar.get()
        gameVersions = mapVersions("modrinth.versions")
        loaders = arrayListOf("bukkit", "spigot", "paper", "folia", "purpur")
        changelog = parseGitHubChangelog()

        syncBodyFrom = file("README.md").readText()
        modrinth.get().dependsOn(modrinthSyncBody)
    }
}

fun mapVersions(propertyName: String): Provider<List<String>> = provider {
    return@provider (property(propertyName) as String).split(",").map { it.trim() }
}

fun parseGitHubChangelog(): Provider<String> = provider {
    val changelog = System.getenv("GITHUB_CHANGELOG")
        ?: return@provider "(No changelog provided)"

    val userRegex = Regex("(?<!\\w)@([A-Za-z0-9-]+)")
    val pullRegex = Regex("https://github\\.com/[\\w-]+/[\\w-]+/pull/(\\d+)")
    val compareRegex = Regex("https://github\\.com/[\\w-]+/[\\w-]+/compare/([\\w\\.]+)")

    changelog.replace(userRegex) {
        val user = it.groupValues[1]
        "[@$user](https://github.com/$user)"
    }.replace(pullRegex) {
        val pr = it.groupValues[1]
        "[#$pr](${it.value})"
    }.replace(compareRegex) {
        val range = it.groupValues[1]
        "[$range](${it.value})"
    }
}
