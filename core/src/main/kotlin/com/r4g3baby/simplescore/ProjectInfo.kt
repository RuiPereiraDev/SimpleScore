package com.r4g3baby.simplescore

import net.swiftzer.semver.SemVer
import java.util.*

object ProjectInfo {
    val name: String
    val version: SemVer

    val githubUser: String
    val githubRepo: String

    init {
        val properties = Properties().apply {
            javaClass.classLoader?.getResource("project.properties")?.let { projectFile ->
                load(projectFile.openStream())
            }
        }

        name = properties.getProperty("name", "")
        version = SemVer.parse(properties.getProperty("version", ""))

        githubUser = properties.getProperty("githubUser", "")
        githubRepo = properties.getProperty("githubRepo", "")
    }
}