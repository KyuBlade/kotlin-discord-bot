package com.omega.discord.bot.command.impl.moderation

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RequestBuffer

class BanCommand : Command() {

    override val name: String = "ban"
    override val aliases: Array<String>? = null
    override val usage: String = "**ban <userMention>** - Ban the mentioned user from the server"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_SKIP_FORCE
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 0

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        if (args.isNotEmpty()) {
            val userMentionStr = args.first()
            val user: IUser? = StringUtils.parseUserMention(userMentionStr)

            if (user != null) {
                val reason: String? = if (args.size > 1) args.drop(1).joinToString(" ") else null

                RequestBuffer.request {
                    try {
                        channel.guild.banUser(user, reason)
                        MessageSender.sendMessage(channel,
                                "**Banned user ${user.mention()}**" +
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