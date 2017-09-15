package com.omega.discord.bot.command

import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer


interface Command {

    val name: String
    val aliases: Array<String>?
    val usage: String
    val allowPrivate: Boolean
    val permission: Permission?
    val ownerOnly: Boolean

    fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>)

    fun missingArgs(author: IUser, message: IMessage) {
        RequestBuffer.request { message.addReaction(ReactionEmoji.of("‼")) }
        MessageSender.sendMessage(author, "One or more arguments are missing\n\n***Usage :***\n\n$usage")
    }
}