package com.omega.discord.bot.command

import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer


abstract class Command {

    abstract val name: String
    abstract val aliases: Array<String>?
    abstract val usage: String
    abstract val allowPrivate: Boolean
    abstract val permission: Permission?
    abstract val ownerOnly: Boolean

    /**
     * Cooldown in seconds before next command for all users
     */
    abstract val globalCooldown: Long

    private var lastCommandTimestamp: Long = 0

    fun executeInternal(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        val newCommandTimestamp = System.currentTimeMillis() / 1000

        // Execute only time since last command in higher than the global cooldown
        if(newCommandTimestamp - lastCommandTimestamp >= globalCooldown /*|| BotManager.applicationOwner == author*/) {

            lastCommandTimestamp = newCommandTimestamp
            execute(author, channel, message, args)
        } else {

            message.addReaction(ReactionEmoji.of("⏱"))
        }
    }

    abstract fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>)

    protected fun missingArgs(author: IUser, message: IMessage) {

        RequestBuffer.request { message.addReaction(ReactionEmoji.of("‼")) }
        MessageSender.sendMessage(author, "One or more arguments are missing\n\n***Usage :***\n\n$usage")
    }
}