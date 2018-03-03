package com.omega.discord.bot.command.impl.music

import com.omega.discord.bot.audio.AudioPlayerManager
import com.omega.discord.bot.audio.NotSeekableException
import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.ext.seek
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder


class SeekCommand : Command() {

    override val name: String = "seek"
    override val aliases: Array<String>? = null
    override val usage: String = "**seek <position>** - Seek to the given position in the playing track (Format: HH:mm:ss)"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_SEEK
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 15

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        val audioManager = AudioPlayerManager.getAudioManager(channel.guild)
        val playingTrack = audioManager.audioPlayer.playingTrack

        if (args.isNotEmpty()) {

            val durationStr = args.first()

            val skipTo = try {

                if (playingTrack == null) {

                    MessageSender.sendMessage(channel, "No track currently playing !")

                    null
                } else {

                    Math.min(
                            StringUtils.parseDuration(durationStr),
                            playingTrack.duration
                    )
                }

            } catch (e: NumberFormatException) {
                MessageSender.sendMessage(channel, "The duration must be in format HH:mm:ss")
                null
            }

            skipTo?.let {
                try {

                    val embedBuilder = EmbedBuilder()

                    val oldPosition = playingTrack.position

                    audioManager.audioPlayer.seek(it)

                    val position = playingTrack.position
                    val duration = playingTrack.duration

                    val fromStr = StringUtils.formatDuration(oldPosition)
                    val toStr = StringUtils.formatDuration(it)
                    val progressStr = "($toStr/${StringUtils.formatDuration(duration)}) " +
                            StringUtils.getTrackAsciiProgressBar(position, duration, 40)

                    embedBuilder
                            .withTitle("Seeking")
                            .appendField("Track", playingTrack.info.title, false)
                            .appendField("From", fromStr, true)
                            .appendField("To", toStr, true)
                            .appendField("Progress", progressStr, false)

                    MessageSender.sendMessage(channel, embedBuilder)
                } catch (e: IllegalStateException) {
                    MessageSender.sendMessage(channel, "No track currently playing !")
                } catch (e: NotSeekableException) {
                    MessageSender.sendMessage(channel, "The playing track is not seekable !")
                }
            }
        } else
            missingArgs(author, message)
    }
}