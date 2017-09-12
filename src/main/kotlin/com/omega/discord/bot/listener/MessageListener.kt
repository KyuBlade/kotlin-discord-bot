package com.omega.discord.bot.listener

import com.omega.discord.bot.command.CommandHandler
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class MessageListener {

    @EventSubscriber
    fun onMessage(event: MessageReceivedEvent) {
        val message = event.message
        val messageContent = message.content

        if (messageContent.startsWith('!')) {
            var indexEndOfCommandName: Int = messageContent.indexOfFirst { it == ' ' }
            if (indexEndOfCommandName == -1) {
                indexEndOfCommandName = messageContent.length
            }

            val commandName: String? = messageContent.substring(1, indexEndOfCommandName)

            if (commandName != null)
                CommandHandler.handle(commandName, message)
        }
    }
}