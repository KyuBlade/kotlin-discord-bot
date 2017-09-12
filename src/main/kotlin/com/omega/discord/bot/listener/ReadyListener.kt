package com.omega.discord.bot.listener

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent


class ReadyListener {

    val LOGGER: Logger = LoggerFactory.getLogger("ReadyListener")

    @EventSubscriber
    fun onReady(event: ReadyEvent) {
        event.client.dispatcher.registerListener(MessageListener())
        LOGGER.info("Bot ready !")
    }
}