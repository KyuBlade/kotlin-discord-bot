package com.omega.discord.bot.command.impl

import com.omega.discord.bot.BotManager
import com.omega.discord.bot.command.Command
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.BotInviteBuilder
import java.util.*


class InviteCommand : Command {
    override val name: String = "invite"
    override val aliases: Array<String>? = null
    override val usage: String = "**invite** - Send a DM with the link to invite the bot"
    override val allowPrivate: Boolean = true
    override val permission: Permission? = Permission.COMMAND_INVITE
    override val ownerOnly: Boolean = false

    private val inviteLink: String = BotInviteBuilder(BotManager.client)
            .withPermissions(
                    EnumSet.of(
                            Permissions.ADD_REACTIONS, Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES,
                            Permissions.VOICE_CONNECT, Permissions.VOICE_SPEAK, Permissions.VOICE_MUTE_MEMBERS
                    )
            ).build()

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {
        MessageSender.sendMessage(author, "Here the link to invite me to your servers : $inviteLink")
    }
}