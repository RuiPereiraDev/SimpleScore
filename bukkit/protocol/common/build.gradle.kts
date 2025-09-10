dependencies {
    api(project(":core"))

    compileOnly(libs.bukkit)
    compileOnly(libs.netty)

    api(libs.objenesis)
}