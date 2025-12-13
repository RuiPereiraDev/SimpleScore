package com.r4g3baby.simplescore.core.util

import com.r4g3baby.simplescore.ProjectInfo
import com.r4g3baby.simplescore.ProjectInfo.GITHUB_REPO
import com.r4g3baby.simplescore.ProjectInfo.GITHUB_USER
import net.swiftzer.semver.SemVer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI

private val URL = URI("https://api.github.com/repos/${GITHUB_USER}/${GITHUB_REPO}/releases/latest").toURL()

fun checkForUpdates(onNewVersion: (SemVer) -> Unit, onNoVersion: (Exception?) -> Unit = {}) {
    val connection = (URL.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 3000
        readTimeout = 3000

        setRequestProperty("Accept", "application/vnd.github.v3+json")
        setRequestProperty("User-Agent", "${ProjectInfo.NAME}/${ProjectInfo.VERSION}")
    }

    try {
        BufferedReader(InputStreamReader(connection.inputStream)).use { inputStream ->
            val response = inputStream.readText()
            val tagNameStartIndex = response.indexOf("\"tag_name\":\"v") + "\"tag_name\":\"v".length
            val tagNameEndIndex = response.indexOf("\"", tagNameStartIndex)

            val tagName = if (tagNameStartIndex > -1 && tagNameEndIndex > -1) {
                response.substring(tagNameStartIndex, tagNameEndIndex)
            } else null

            if (tagName != null) {
                val latestVersion = SemVer.parse(tagName)
                if (ProjectInfo.VERSION < latestVersion) {
                    onNewVersion(latestVersion)
                } else onNoVersion(null)
            } else onNoVersion(null)
        }
    } catch (ex: Exception) {
        onNoVersion(ex)
    } finally {
        connection.disconnect()
    }
}