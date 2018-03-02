package com.omega.discord.bot.command.impl.music

import com.omega.discord.bot.BotManager
import com.omega.discord.bot.audio.AudioPlayerManager
import com.omega.discord.bot.audio.TrackUserData
import com.omega.discord.bot.command.Command
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.permission.PermissionManager
import com.omega.discord.bot.util.MessageSender
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.handle.obj.IVoiceChannel
import sx.blah.discord.util.RequestBuffer


class SkipCommand : Command {

    override val name: String = "skip"
    override val aliases: Array<String>? = null
    override val usage: String = "**skip** - Skip the playing track\n" +
            "**skip force** - Skip the playing track without voting **(Need to be server owner or have permission command.skip.force)**\n" +
            "**skip <count>** - Skip nth tracks **(Need to be server owner or have permission command.skip.multiple)**"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_SKIP
    override val ownerOnly: Boolean = false

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        val audioManager = AudioPlayerManager.getAudioManager(channel.guild)
        val voiceChanel: IVoiceChannel? = channel.guild.connectedVoiceChannel
        val playingTrack: AudioTrack? = audioManager.audioPlayer.playingTrack

        if (voiceChanel == null) {

            MessageSender.sendMessage(channel, "Not connected to a voice channel")
        } else {

            val forceSkip: Boolean = args.firstOrNull()?.equals("force", true) ?: false

            if (args.isEmpty()) { // Vote

                if (BotManager.client.connectedVoiceChannels.contains(author.getVoiceStateForGuild(channel.guild)?.channel)) {

                    if (playingTrack == null) {

                        MessageSender.sendMessage(channel, "Not playing anything")
                        return
                    }

                    val trackUserData = playingTrack.userData as TrackUserData
                    val votes: MutableSet<IUser> = trackUserData.skipVotes

                    votes.add(author)

                    val neededCount: Int = Math.ceil(voiceChanel.connectedUsers.size * 0.5).toInt()
                    val currentVotes: Int = votes.size

                    if (currentVotes >= neededCount) {

                        audioManager.scheduler.skip(1)
                        MessageSender.sendMessage(channel, "Skipped track ${playingTrack.info.title}")
                    } else
                        MessageSender.sendMessage(channel, "Vote to skip : $currentVotes / $neededCount")
                }

            } else if (forceSkip) { // Force

                if (playingTrack == null) {

                    MessageSender.sendMessage(channel, "Not playing anything")
                    return
                }

                if (author == BotManager.applicationOwner || author == message.guild.owner ||
                        PermissionManager.hasPermission(message.guild, author, Permission.COMMAND_SKIP_FORCE)) {

                    audioManager.scheduler.skip(1)
                    MessageSender.sendMessage(channel, "Skipped track ${playingTrack.info.title} (forced)")
                } else {

                    RequestBuffer.request { message.addReaction(ReactionEmoji.of("⛔")) }
                }
            } else { // Multiple skip

                if (playingTrack == null) {

                    MessageSender.sendMessage(channel, "Not playing anything")
                    return
                }

                try {

                    val skipCount = args[0].toInt()
                    val skippedCount = audioManager.scheduler.skip(skipCount)

                    MessageSender.sendMessage(channel, "Skipped $skippedCount tracks")

                } catch (e: NumberFormatException) {

                    MessageSender.sendMessage(channel, "The count parameter '${args[0]}' is not a number")
                }
            }
        }
    }
}