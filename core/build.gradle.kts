dependencies {
    api(project(":api"))

    api(libs.semver)
}

kotlin {
    sourceSets.main {
        kotlin.srcDir(
            tasks.named("generateProjectInfo").get().outputs
        )
    }
}

tasks {
    compileKotlin {
        dependsOn("generateProjectInfo")
    }
}