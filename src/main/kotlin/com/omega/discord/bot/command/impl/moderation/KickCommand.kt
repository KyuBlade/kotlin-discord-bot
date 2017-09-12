package com.omega.discord.bot.command.impl.moderation

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RequestBuffer


class KickCommand : Command {

    override val name: String = "kick"
    override val aliases: Array<String>? = null
    override val usage: String = "**kick <userMention>** - Kick the mentioned user from the server"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_SKIP_FORCE
    override val ownerOnly: Boolean = false

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        if (args.isNotEmpty()) {
            val userMentionStr = args.first()
            val user: IUser? = StringUtils.parseUserMention(userMentionStr)

            if (user != null) {
                val reason: String? = if (args.size > 1) args.drop(1).joinToString(" ") else null

                RequestBuffer.request {
                    try {
                        channel.guild.kickUser(user, reason)
                        MessageSender.sendMessage(channel,
                                "**Kicked user ${user.mention()}**" +
                                        if (reason != null) "\n**Reason:** $reason" else ""
                        )
                    } catch (e: MissingPermissionsException) {
                        MessageSender.sendMessage(channel, e.errorMessage)
                    }
                }
            } else {
                MessageSender.sendMessage(channel, "User $userMentionStr not found")
            }
        } else {
            missingArgs(author, message)
        }
    }
}