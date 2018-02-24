package com.omega.discord.bot.listener

import com.omega.discord.bot.command.CommandHandler
import com.omega.discord.bot.property.GuildProperty
import com.omega.discord.bot.property.GuildPropertyManager
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class MessageCommandListener {

    @EventSubscriber
    fun onMessage(event: MessageReceivedEvent) {

        val message = event.message
        val messageContent = message.content

        val guild = event.guild
        val prefix = if (guild != null)
            GuildPropertyManager.get(event.guild, GuildProperty.COMMAND_PREFIX).value as String
        else
            "!"

        if (messageContent.startsWith(prefix)) {

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