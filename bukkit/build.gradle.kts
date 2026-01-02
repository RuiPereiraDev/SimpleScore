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
