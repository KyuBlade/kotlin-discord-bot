package com.omega.discord.bot.command.impl.moderation

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import java.time.LocalDateTime
import java.time.ZoneOffset


class PurgeCommand : Command() {

    override val name: String = "purge"
    override val aliases: Array<String>? = null
    override val usage: String = "**Discord only allow to delete messages that are younger than 2 weeks\n" +
            "\n" +
            "**purge <count>** - Will delete the provided amount of last messages from the current channel\n" +
            "**purge <userMention>** - Will delete x last messages of the mention user from the current channel\n" +
            "**purge <userMention> <count>** - Will delete the privived number of last messages of the mention user from the current channem\n" +
            "**purge <userMention> <channelMention>** - Will delete x last messages of the mention user from the mentioned channel\n" +
            "**purge <userMention> <channelMention> <count>** - Will delete the privided number of last messages of the mention user from the mentioned channel"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_PURGE
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 60

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        when (args.size) {

            1 -> {

                val arg = args[0]

                try {
                    val count = Integer.valueOf(arg)
                    val deletedMessages = channel.getMessageHistory(count + 1).bulkDelete()

                    MessageSender.sendMessage(channel, "Deleted ${deletedMessages.size - 1}/$count messages.")

                } catch (e: NumberFormatException) {

                    try {
                        val user: IUser? = StringUtils.parseUserMention(arg)

                        if (user == null) {

                            MessageSender.sendMessage(channel, "User $arg not found.")
                        } else {

                            val messages = channel.messageHistory
                                    .filter { it.author == user }

                            val deletedMessages = channel.bulkDelete(messages)

                            MessageSender.sendMessage(channel, "Deleted ${deletedMessages.size} messages from user ${user.mention()}.")
                        }

                    } catch (e: NumberFormatException) {

                        MessageSender.sendMessage(channel, "You should provide a count or an user mention, got: $arg")
                    }
                }
            }
            2 -> {

                val userMention = args[0]
                val countStr = args[1]
                val user: IUser?
                val count: Int

                // Get user
                try {
                    user = StringUtils.parseUserMention(userMention)

                    if (user == null) {

                        MessageSender.sendMessage(channel, "User $userMention not found.")
                        return
                    }
                } catch (e: NumberFormatException) {

                    MessageSender.sendMessage(channel, "You should provide an user mention as first parameter, got: $userMention")
                    return
                }

                // Get count
                try {
                    count = Integer.valueOf(countStr)
                } catch (e: NumberFormatException) {

                    MessageSender.sendMessage(channel, "Format of the count parameter should be a valid integer, got: $countStr")
                    return
                }

                var date = LocalDateTime.now()
                var day = 0
                val messages: MutableList<IMessage> = mutableListOf()

                do {
                    date = date.minusDays(1)
                    day++

                    println("Get messages from $day days ago")
                    messages.addAll(channel.getMessageHistoryTo(date.toInstant(ZoneOffset.ofHours(2)))
                            .filter { it.author == user })
                    println("Now got ${messages.size} messages")

                } while (messages.size < count && day <= 14)

                val deletedMessages = channel.bulkDelete(messages.takeLast(count))

                MessageSender.sendMessage(channel, "Deleted ${deletedMessages.size}/$count messages from user ${user.mention()}.")
            }
        }
    }
}