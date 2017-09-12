package com.omega.discord.bot.audio.loader

import com.omega.discord.bot.audio.GuildAudioPlayerManager
import com.omega.discord.bot.util.MessageSender
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sx.blah.discord.handle.obj.IChannel


class QueueUrlResultLoadHandler(private val manager: GuildAudioPlayerManager, val channel: IChannel) : AudioLoadHandler() {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun trackLoaded(track: AudioTrack) {
        super.trackLoaded(track)

        MessageSender.sendMessage(channel, "Added track ${track.info.title} to queue")
        manager.scheduler.queue(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        super.playlistLoaded(playlist)

        MessageSender.sendMessage(channel, "Added playlist ${playlist.name} (${playlist.tracks.size} tracks) to queue")
        manager.scheduler.queue(playlist.tracks)
    }

    override fun noMatches() {
        super.noMatches()

        MessageSender.sendMessage(channel, "No track(s) found")
    }

    override fun loadFailed(exception: FriendlyException?) {
        super.loadFailed(exception)

        MessageSender.sendMessage(channel, "Failed to load track(s)")
        LOGGER.error("Failed to load track(s)", exception)
    }
}