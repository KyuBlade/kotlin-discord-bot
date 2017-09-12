package com.omega.discord.bot.ext

import com.omega.discord.bot.BotConfiguration
import java.io.File
import java.net.URI


fun getJarPath(): File {
    val jarPath: URI = BotConfiguration::class.java.protectionDomain.codeSource.location.toURI()
    if (jarPath.path.contains("build") || jarPath.path.contains("production")) { // Launched from IDE
        return File(System.getProperty("user.dir"))
    } else {
        return File(jarPath).parentFile
    }


}