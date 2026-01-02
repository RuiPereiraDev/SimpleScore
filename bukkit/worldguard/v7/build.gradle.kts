dependencies {
    api(project(":bukkit:worldguard:common"))

    compileOnly(libs.bukkit)
    compileOnly(libs.worldguardV7) {
        exclude(module = "bukkit")
    }
}
