package com.omega.discord.bot.command.impl.config

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.property.GuildProperty
import com.omega.discord.bot.property.GuildPropertyManager
import com.omega.discord.bot.property.type.StringPropertyValue
import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser


class PrefixCommand : Command() {

    override val name: String = "prefix"
    override val aliases: Array<String>? = null
    override val usage: String = "**prefix <prefix>** - Set the bot command prefix"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_PREFIX
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 0

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        if(args.isEmpty()) {

            missingArgs(author, message)
        } else {

            val prefix = args.first()

            if(GuildPropertyManager.set(channel.guild, GuildProperty.COMMAND_PREFIX, StringPropertyValue(prefix)))
                MessageSender.sendMessage(channel, "Bot prefix set to $prefix")
            else
                MessageSender.sendMessage(channel, "Unable to set prefix")
        }
    }
}