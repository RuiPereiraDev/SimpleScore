import org.apache.tools.ant.filters.ReplaceTokens

dependencies {
    api(project(":core"))
    implementation(project(":bukkit:protocol"))
    implementation(project(":bukkit:worldguard"))

    compileOnly(libs.bukkit)
    compileOnly(libs.papi)
    compileOnly(libs.adventureMiniMessage)
    compileOnly(libs.adventureSerializer)

    implementation(libs.bStatsBukkit)
}

subprojects {
    group = "${project.group}.bukkit"
}

tasks {
    processResources {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
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
