package com.omega.discord.bot.command

import com.omega.discord.bot.BotManager
import com.omega.discord.bot.ext.getNameAndDiscriminator
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
            RequestBuffer.request { message.addReaction(ReactionEmoji.of("⁉")) }
        } else {
            val permission: Permission? = command.permission
            if (permission == null || message.author == BotManager.applicationOwner ||
                    PermissionManager.getPermissions(message.guild, message.author).has(permission)) {

                if (!command.allowPrivate && message.channel.isPrivate) {
                    RequestBuffer.request { message.addReaction(ReactionEmoji.of("\uD83D\uDEB7")) }
                } else {
                    try {
                        command.execute(message.author, message.channel, message,
                                message.content.split(' ').drop(1))
                    } catch (e: Exception) {
                        LOGGER.warn("Command execution failed", e)
                        RequestBuffer.request { message.addReaction(ReactionEmoji.of("\uD83C\uDD98")) }
                    }
                }

            } else {// No permission
                LOGGER.info("User ${message.author.getNameAndDiscriminator()} doesn't have permission ${command.permission} to run command $commandName")
                RequestBuffer.request { message.addReaction(ReactionEmoji.of("⛔")) }
            }
        }
    }
}