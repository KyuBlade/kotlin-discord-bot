package com.omega.discord.bot.command.impl.music

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.handle.obj.*
import sx.blah.discord.util.PermissionUtils
import sx.blah.discord.util.RequestBuffer


class JoinCommand : Command() {

    override val name: String = "join"
    override val aliases: Array<String>? = arrayOf("j")
    override val usage: String = "**join** - Join your current voice channel\n" +
            "**join <voice_channel_name>** - Join the named voice channel"
    override val allowPrivate: Boolean = false
    override val permission: Permission = Permission.COMMAND_JOIN
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 15

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        val voiceChannel: IVoiceChannel?

        if (args.isNotEmpty()) {

            val channelName = args.joinToString(" ")

            voiceChannel = message.guild.getVoiceChannelsByName(channelName).firstOrNull()

            if (voiceChannel == null)
                MessageSender.sendMessage(channel, "No voice channels named $channelName found")

        } else {

            voiceChannel = author.getVoiceStateForGuild(channel.guild).channel

            if (voiceChannel == null)
                MessageSender.sendMessage(channel, "You are not in a voice channel")
        }

        if (voiceChannel != null) {

            when {
                !PermissionUtils.hasPermissions(voiceChannel, channel.client.ourUser, Permissions.VOICE_CONNECT) ->
                    MessageSender.sendMessage(channel, "Can't join the voice channel ${voiceChannel.name} : Missing permission VOICE_CONNECT")

                voiceChannel.isConnected ->
                    MessageSender.sendMessage(channel, "Already connected to this channel")

                else ->
                    RequestBuffer.request {

                        voiceChannel.join()
                        MessageSender.sendMessage(channel, "Joined channel **${voiceChannel.name}**")
                    }
            }
        }
    }
}