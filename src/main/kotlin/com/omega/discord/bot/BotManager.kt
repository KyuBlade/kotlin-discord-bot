package com.omega.discord.bot

import com.omega.discord.bot.database.DatabaseFactory
import com.omega.discord.bot.listener.ReadyListener
import com.omega.discord.bot.permission.PermissionManager
import com.omega.discord.bot.property.GuildPropertyManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.obj.IUser
import java.lang.IllegalArgumentException
import java.nio.file.NoSuchFileException


object BotManager {

    val LOGGER: Logger = LoggerFactory.getLogger("Main")

    lateinit var botConfig: BotConfiguration
        private set
    lateinit var client: IDiscordClient
        private set
    lateinit var applicationOwner: IUser
        private set

    fun start() {
        try {
            botConfig = BotConfiguration()
            client = ClientBuilder()
                    .withToken(botConfig.getToken())
                    .registerListeners(this, ReadyListener())
                    .build()

            // Init database factory
            DatabaseFactory

            // Init permissions manager
            PermissionManager

            // Init guild properties manager
            GuildPropertyManager

            client.login()
        } catch (e: NoSuchFileException) {
            LOGGER.error("Bot configuration file not found (Must be created before starting the bot) : ${e.file}")
        } catch (e: IllegalArgumentException) {
            LOGGER.error("Malformed properties : {}", e.message)
        } catch (e: Exception) {
            LOGGER.error("Something went wrong when loading bot configuration", e)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @EventSubscriber
    fun onReady(event: ReadyEvent) {
        applicationOwner = client.applicationOwner
        LOGGER.debug("Application owner set : ${applicationOwner.name}")
    }
}