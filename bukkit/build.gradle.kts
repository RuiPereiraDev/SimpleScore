plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":core"))

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    // compileOnly(libs.bukkit)
    compileOnly(libs.netty)

    implementation(libs.bStatsBukkit)
}
