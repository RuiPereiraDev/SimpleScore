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

val bStatsBukkitId = 644
val downloadUrl = "https://modrinth.com/plugin/simplescore"
val githubUser = "r4g3baby"
val githubRepo = "SimpleScore"

dependencies {
    api(project("bukkit"))
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "org.jetbrains.kotlin.jvm")
}

allprojects {
    kotlin {
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(8)
        }
    }

    tasks {
        register("generateProjectInfo") {
            val path = "${rootProject.group}.${rootProject.name.lowercase()}"
            val outputDir = layout.buildDirectory.dir("generated/source/projectInfo/$path")
            outputs.dir(outputDir)

            val outputFile = outputDir.map { it.file("ProjectInfo.kt") }.get().asFile
            doLast {
                outputFile.parentFile.mkdirs()
                outputFile.writeText(
                    """
                        package $path

                        import net.swiftzer.semver.SemVer

                        object ProjectInfo {
                            const val BSTATS_BUKKIT_ID: Int = $bStatsBukkitId

                            const val NAME: String = "${rootProject.name}"
                            val VERSION: SemVer = SemVer.parse("${rootProject.version}")

                            const val DOWNLOAD_URL: String = "$downloadUrl"
                            const val GITHUB_USER: String = "$githubUser"
                            const val GITHUB_REPO: String = "$githubRepo"
                        }
                    """.trimIndent()
                )
            }
        }

        processResources {
            filteringCharset = "UTF-8"
            filesMatching(listOf("**plugin.yml")) {
                filter<ReplaceTokens>(
                    "tokens" to mapOf(
                        "name" to rootProject.name,
                        "version" to rootProject.version,
                        "description" to "A simple animated scoreboard plugin for your server.",
                        "package" to "${rootProject.group}.${rootProject.name.lowercase()}",
                        "website" to "https://r4g3baby.com"
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

    val repoUrl = findProperty("github.repo") as String? ?: System.getenv("GITHUB_REPO_URL")
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