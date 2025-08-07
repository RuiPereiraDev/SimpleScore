subprojects {
    group = "${rootProject.group}.protocol"
}

dependencies {
    api(project(":bukkit:protocol:modern"))
    api(project(":bukkit:protocol:legacy"))
}