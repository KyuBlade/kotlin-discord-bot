package com.omega.discord.bot

import com.omega.discord.bot.ext.getJarPath
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

val CONFIG_PATH: Path = Paths.get(getJarPath().path, "config.properties")

const val TOKEN_KEY: String = "token"
const val SHARDS_KEY: String = "shards"
const val HOST_KEY: String = "host"
const val PORT_KEY: String = "port"
const val DATABASE_KEY: String = "database"
const val USRE_KEY: String = "user"
const val PASSWORD_KEY: String = "password"

class BotConfiguration {

    lateinit var properties: Properties

    init {
        load()
    }

    private fun load() {
        properties = Properties()

        val fis = Files.newInputStream(CONFIG_PATH)
        fis.use {
            properties.load(fis)
        }
    }

    fun save() {
        val fos = Files.newOutputStream(CONFIG_PATH)
        fos.use {
            properties.store(fos, "Bot configuration")
        }
    }

    fun getToken(): String? = properties.getProperty(TOKEN_KEY)

    fun getShards(): Int = Integer.parseInt(properties.getProperty(SHARDS_KEY, "1"))

    fun getHost(): String = properties.getProperty(HOST_KEY, "localhost")

    fun getPort(): Int = Integer.parseInt(properties.getProperty(PORT_KEY, "3306"))

    fun getDatabase(): String? = properties.getProperty(DATABASE_KEY)

    fun getUser(): String = properties.getProperty(USRE_KEY, "root")

    fun getPassword(): String = properties.getProperty(PASSWORD_KEY, "")
}