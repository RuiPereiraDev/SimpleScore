import org.apache.tools.ant.filters.ReplaceTokens

dependencies {
    api(project(":core"))
    implementation(project(":bukkit:worldguard"))

    compileOnly(libs.bukkit)
    compileOnly(libs.netty)
    compileOnly(libs.papi)
    compileOnly(libs.adventureMiniMessage)
    compileOnly(libs.adventureSerializer)

    implementation(libs.objenesis)
    implementation(libs.bStatsBukkit)
}

tasks {
    processResources {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            filter<ReplaceTokens>(
                "tokens" to mapOf(
                    "name" to rootProject.name,
                    "version" to rootProject.version,
                    "package" to rootProject.group,
                    "description" to "A simple animated scoreboard plugin for your server.",
                    "website" to "https://ruipereira.dev"
                )
            )
        }
    }
}
