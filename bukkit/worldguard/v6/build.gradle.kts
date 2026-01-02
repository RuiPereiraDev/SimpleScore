dependencies {
    api(project(":bukkit:worldguard:common"))

    compileOnly(libs.bukkit)
    compileOnly(libs.worldguardV6) {
        exclude(module = "bukkit")
    }
}
