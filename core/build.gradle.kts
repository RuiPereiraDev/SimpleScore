plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":api"))

    api(libs.semver)
}
