package com.omega.discord.bot.command.impl.music

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.handle.obj.IVoiceChannel
import sx.blah.discord.util.RequestBuffer


class LeaveCommand : Command() {

    override val name: String = "leave"
    override val aliases: Array<String>? = arrayOf("l")
    override val usage: String = "**leave** - Leave the voice channel"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_LEAVE
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 15

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {
        RequestBuffer.request {
            val voiceChannel: IVoiceChannel? = message.guild.connectedVoiceChannel
            if (voiceChannel != null) {
                voiceChannel.leave()
                MessageSender.sendMessage(channel, "Left voice channel")
            } else
                MessageSender.sendMessage(channel, "Not connected to a voice channel")
        }
    }
}