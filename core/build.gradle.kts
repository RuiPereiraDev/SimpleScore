dependencies {
    api(project(":api"))

    api(libs.semver)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

val generateProjectInfo = tasks.register("generateProjectInfo") {
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
                    const val BSTATS_BUKKIT_ID: Int = ${rootProject.property("bStats.bukkit")}

                    const val NAME: String = "${rootProject.name}"
                    val VERSION: SemVer = SemVer.parse("${rootProject.version}")

                    const val DOWNLOAD_URL: String = "https://modrinth.com/plugin/simplescore"
                    const val GITHUB_USER: String = "${rootProject.property("github.user")}"
                    const val GITHUB_REPO: String = "${rootProject.property("github.repo")}"
                }
            """.trimIndent()
        )
    }
}

tasks {
    compileKotlin {
        dependsOn(generateProjectInfo)
    }
}

kotlin {
    sourceSets.main {
        kotlin.srcDir(generateProjectInfo.get().outputs)
    }
}