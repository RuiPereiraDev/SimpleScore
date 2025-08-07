dependencies {
    api(project(":bukkit:worldguard:api"))

    compileOnly(libs.bukkit)
    compileOnly(libs.worldguardV6) {
        exclude(module = "bukkit")
    }
}