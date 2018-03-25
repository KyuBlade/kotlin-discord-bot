package com.omega.discord.bot.command.impl.common

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser


class SayCommand : Command() {

    override val name: String = "say"
    override val aliases: Array<String>? = null
    override val usage: String = "**say <message>** - Make the bot say the message on the current channel\n" +
            "**say <chanelMention> <message>** - Make the bot say the message on the defined channel"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = null
    override val ownerOnly: Boolean = true
    override val globalCooldown: Long = 0

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        message.delete()

        when {
            // Missing arguments
            args.isEmpty() -> missingArgs(author, message)

            // Channel mention provided
            StringUtils.isChannelMention(args.first()) -> {

                if(args.size > 1) {

                    val targetChannel: IChannel? = StringUtils.parseChannelMention(args.first())

                    targetChannel?.let {
                        MessageSender.sendMessage(it, args.drop(1).joinToString(" "))
                    } ?: MessageSender.sendMessage(author, "Channel not found")

                } else {

                    missingArgs(author, message)
                }
            }

            // Send on current channel
            else -> MessageSender.sendMessage(channel, args.joinToString(" "))
        }
    }
}