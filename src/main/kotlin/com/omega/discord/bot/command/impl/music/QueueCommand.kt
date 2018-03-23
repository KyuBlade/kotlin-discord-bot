package com.omega.discord.bot.command.impl.music

import com.omega.discord.bot.audio.AudioPlayerManager
import com.omega.discord.bot.audio.GuildAudioPlayerManager
import com.omega.discord.bot.audio.loader.QueueKeywordLoadHandler
import com.omega.discord.bot.audio.loader.QueueUrlResultLoadHandler
import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.service.MessageSender
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser


class QueueCommand : Command() {

    private val LOGGER: Logger = LoggerFactory.getLogger(QueueCommand::class.java)

    override val name: String = "queue"
    override val aliases: Array<String>? = arrayOf("q")
    override val usage: String = "**queue <url>** - Add the url track to the queue\n" +
            "**queue <keywords>** - Search on youtube for the given keywords and add the first result to the queue\n" +
            "**queue clear** - Clear the queue"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_QUEUE
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 5

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        val audioPlayerManager: GuildAudioPlayerManager = AudioPlayerManager.getAudioManager(channel.guild)

        if (args.isEmpty()) {

            val tracks: List<AudioTrack> = audioPlayerManager.scheduler.getQueuedTracks(0..9)

            val builder = StringBuilder()

            val playingTrack: AudioTrack? = audioPlayerManager.audioPlayer.playingTrack

            if (playingTrack != null) {

                val trackInfo = playingTrack.info

                with(builder) {
                    append("**Currently playing track :** \n\n")
                    append('\t').append(trackInfo.title).append('\n')
                    append("\t(${StringUtils.formatDuration(playingTrack.position)}/${StringUtils.formatDuration(playingTrack.duration)}) ")
                    append(StringUtils.getTrackAsciiProgressBar(playingTrack.position, playingTrack.duration))
                    append("\n\n")
                }
            }

            builder.append("**Tracks in queue :** \n\n")

            if (tracks.isEmpty()) {
                builder.append("\tNo tracks in queue")
            } else {

                for (i in 0 until tracks.size) {
                    val track: AudioTrack = tracks[i]
                    builder.append("\t[${i + 1}] > ${track.info.title}(${StringUtils.formatDuration(track.duration)})\n")
                }

                val queueSize = audioPlayerManager.scheduler.queueSize()
                if (queueSize > 10)
                    builder.append("\n\t==> ${queueSize - 10} more ...")
            }

            MessageSender.sendMessage(channel, builder.toString())

        } else {

            val firstArg = args.first()

            if (firstArg.startsWith("http") || firstArg.startsWith("www.")) { // URL detected

                val taskMessage = MessageSender.sendMessage(channel, "Loading ...")

                audioPlayerManager.manager.loadItemOrdered(
                        audioPlayerManager,
                        firstArg,
                        QueueUrlResultLoadHandler(audioPlayerManager, taskMessage)
                )
            } else if (firstArg.startsWith("clear")) { // Clear queue

                audioPlayerManager.scheduler.clear()
                MessageSender.sendMessage(channel, "Queue cleared")
            } else { // Keywords search

                val taskMessage = MessageSender.sendMessage(channel, "Loading ...")

                audioPlayerManager.manager.loadItemOrdered(
                        audioPlayerManager,
                        args.joinToString(" ", "ytsearch:"),
                        QueueKeywordLoadHandler(audioPlayerManager, taskMessage)
                )
            }
        }
    }
}