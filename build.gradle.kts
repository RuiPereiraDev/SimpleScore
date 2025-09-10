import io.papermc.hangarpublishplugin.model.Platforms
import org.apache.tools.ant.filters.ReplaceTokens
import java.io.ByteArrayOutputStream

plugins {
    id("maven-publish")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
}

group = "com.r4g3baby"
version = "4.0.0-dev"

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

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()

                    from(components["java"])
                }
            }
        }
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

        val libs = "${project.group}.${project.name.lowercase()}.lib"
        relocate("org.objenesis", "$libs.objenesis")
        relocate("net.swiftzer.semver", "$libs.semver")
        relocate("org.bstats", "$libs.bstats")
        //relocate("com.zaxxer.hikari", "$libs.hikari")
        //relocate("org.slf4j", "$libs.slf4j")

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
            changelog = generateChangelog()

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
        loaders = arrayListOf("bukkit", "spigot", "paper", "folia")
        changelog = generateChangelog()

        syncBodyFrom = file("README.md").readText()
        modrinth.get().dependsOn(modrinthSyncBody)
    }
}

fun mapVersions(propertyName: String): Provider<List<String>> = provider {
    return@provider (property(propertyName) as String).split(",").map { it.trim() }
}

fun generateChangelog(): Provider<String> = provider {
    val tags = ByteArrayOutputStream().apply {
        providers.exec {
            commandLine("git", "tag", "--sort", "version:refname")
            standardOutput = this@apply
        }
    }.toString(Charsets.UTF_8.name()).trim().split("\n")

    val tagsRange = if (tags.size > 1) {
        "${tags[tags.size - 2]}...${tags[tags.size - 1]}"
    } else if (tags.isNotEmpty()) tags[0] else "HEAD~1...HEAD"

    val repoUrl = property("github.url") as String?
    val changelog = ByteArrayOutputStream().apply {
        write("### Commits:\n".toByteArray())

        providers.exec {
            commandLine("git", "log", tagsRange, "--pretty=format:- [%h]($repoUrl/commit/%H) %s", "--reverse")
            standardOutput = this@apply
        }

        write("\n\nCompare Changes: [$tagsRange]($repoUrl/compare/$tagsRange)".toByteArray())
    }.toString(Charsets.UTF_8.name())

    return@provider changelog
}