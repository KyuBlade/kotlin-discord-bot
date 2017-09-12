package com.omega.discord.bot.command

import com.omega.discord.bot.BotManager
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.permission.PermissionManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.RequestBuffer
import java.lang.Exception

object CommandHandler {

    private val LOGGER: Logger = LoggerFactory.getLogger(CommandHandler.javaClass)

    fun handle(commandName: String, message: IMessage) {
        val command: Command? = CommandRegistry.get(commandName)
        if (command == null) {
            LOGGER.info("Command $commandName not found")
            RequestBuffer.request { message.addReaction(ReactionEmoji.of(":interrobang:")) }
        } else {
            val permission: Permission? = command.permission
            if (permission == null || message.author == BotManager.applicationOwner ||
                    PermissionManager.getPermissions(message.guild, message.author).has(permission)) {

                if (!command.allowPrivate && message.channel.isPrivate) {
                    RequestBuffer.request { message.addReaction(ReactionEmoji.of(":no_pedestrians:")) }
                } else {
                    try {
                        command.execute(message.author, message.channel, message,
                                message.content.split(' ').drop(1))
                    } catch (e: Exception) {
                        LOGGER.warn("Command execution failed", e)
                        RequestBuffer.request { message.addReaction(ReactionEmoji.of(":sos:")) }
                    }
                }

            } else // No permission
                RequestBuffer.request { message.addReaction(ReactionEmoji.of(":no_entry:")) }
        }
    }
}