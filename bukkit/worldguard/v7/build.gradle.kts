dependencies {
    api(project(":bukkit:worldguard:api"))

    compileOnly(libs.bukkit)
    compileOnly(libs.worldguardV7) {
        exclude(module = "bukkit")
    }
}