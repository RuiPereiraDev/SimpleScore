import io.papermc.hangarpublishplugin.model.Platforms
import org.apache.tools.ant.filters.ReplaceTokens
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
}

group = "com.r4g3baby"
version = "4.0.0-dev"

repositories {
    mavenCentral()
}

dependencies {
    api(project("bukkit"))
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}

allprojects {
    afterEvaluate {
        kotlin {
            jvmToolchain {
                languageVersion = JavaLanguageVersion.of(17)
            }
        }

        tasks {
            processResources {
                filteringCharset = "UTF-8"
                filesMatching(listOf("**plugin.yml", "**project.properties")) {
                    filter<ReplaceTokens>(
                        "tokens" to mapOf(
                            "name" to rootProject.name,
                            "version" to rootProject.version,
                            "description" to "A simple animated scoreboard plugin for your server.",
                            "package" to "${rootProject.group}.${rootProject.name.lowercase()}",
                            "website" to "https://r4g3baby.com",
                            "githubUser" to "r4g3baby", "githubRepo" to "SimpleScore"
                        )
                    )
                }
            }
        }
    }
}

tasks {
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")

        val libs = "${project.group}.${project.name.lowercase()}.lib"
        relocate("org.codemc.worldguardwrapper", "$libs.wgwrapper")
        relocate("net.swiftzer.semver", "$libs.semver")
        relocate("org.bstats", "$libs.bstats")
        relocate("com.zaxxer.hikari", "$libs.hikari")
        relocate("org.slf4j", "$libs.slf4j")
        relocate("org.jetbrains", "$libs.jetbrains")
        relocate("org.intellij", "$libs.intellij")
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
            id = findProperty("hangar.project") as String? ?: System.getenv("HANGAR_PROJECT")
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
        projectId = findProperty("modrinth.project") as String? ?: System.getenv("MODRINTH_PROJECT")
        uploadFile = shadowJar.get()
        gameVersions = mapVersions("modrinth.versions")
        loaders = arrayListOf("bukkit", "spigot", "paper")
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
        exec {
            commandLine("git", "tag", "--sort", "version:refname")
            standardOutput = this@apply
        }
    }.toString(Charsets.UTF_8.name()).trim().split("\n")

    val tagsRange = if (tags.size > 1) {
        "${tags[tags.size - 2]}...${tags[tags.size - 1]}"
    } else if (tags.isNotEmpty()) tags[0] else "HEAD~1...HEAD"

    val repoUrl = findProperty("github.repo") as String? ?: System.getenv("GITHUB_REPO_URL")
    val changelog = ByteArrayOutputStream().apply {
        write("### Commits:\n".toByteArray())

        exec {
            commandLine("git", "log", tagsRange, "--pretty=format:- [%h]($repoUrl/commit/%H) %s", "--reverse")
            standardOutput = this@apply
        }

        write("\n\nCompare Changes: [$tagsRange]($repoUrl/compare/$tagsRange)".toByteArray())
    }.toString(Charsets.UTF_8.name())

    return@provider changelog
}