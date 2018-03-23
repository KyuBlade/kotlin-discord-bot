package com.omega.discord.bot.command.impl.common

import com.omega.discord.bot.BotManager
import com.omega.discord.bot.command.Command
import com.omega.discord.bot.command.CommandRegistry
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.permission.PermissionManager
import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser


class HelpCommand : Command() {

    override val name: String = "help"
    override val aliases: Array<String>? = arrayOf("h")
    override val usage: String = "**help** - Print all available commands\r**help <commandName>** Print usage of a command"
    override val permission: Permission? = null
    override val allowPrivate: Boolean = true
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 0

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        if (args.isNotEmpty()) {

            commandHelp(author, args[0].toLowerCase())
        } else {
            commandHelp(channel, author)
        }
    }

    private fun commandHelp(channel: IChannel, author: IUser) {

        val builder = StringBuilder("Use !help <commandName> to get a command usage\n\n**Available commands :**\n```")

        CommandRegistry.get().forEach {

            if (it.permission == null ||
                    author == BotManager.applicationOwner ||
                    (!channel.isPrivate && author == channel.guild.owner) ||
                    if (channel.isPrivate) true else PermissionManager.hasPermission(channel.guild, author, it.permission!!)) {

                builder.append(it.name)

                it.aliases?.forEach { builder.append(", ").append(it) }

                builder.append('\n')

            }

        }
        builder.append("```")

        MessageSender.sendMessage(author, builder.toString())
    }

    private fun commandHelp(author: IUser, commandName: String) {

        val command: Command? = CommandRegistry.get(commandName)
        if (command != null) {
            val builder = StringBuilder("Usage of command ${command.name} :\n\n")
            builder.append(command.usage)

            MessageSender.sendMessage(author, builder.toString())
        } else {
            MessageSender.sendMessage(author, "No command with name $commandName found")
        }
    }
}