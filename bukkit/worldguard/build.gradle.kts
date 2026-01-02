dependencies {
    api(project(":core"))
    api(project(":bukkit:worldguard:api"))

    implementation(project(":bukkit:worldguard:v6"))
    implementation(project(":bukkit:worldguard:v7"))

    compileOnly(libs.bukkit)
}
