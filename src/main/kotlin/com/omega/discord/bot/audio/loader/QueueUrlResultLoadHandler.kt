package com.omega.discord.bot.audio.loader

import com.omega.discord.bot.audio.GuildAudioPlayerManager
import com.omega.discord.bot.service.MessageSender
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class QueueUrlResultLoadHandler(private val manager: GuildAudioPlayerManager, val message: MessageSender.Message) : AudioLoadHandler() {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun trackLoaded(track: AudioTrack) {
        super.trackLoaded(track)

        message.edit("Added track ${track.info.title} to queue")
        manager.scheduler.queue(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        super.playlistLoaded(playlist)

        message.edit("Added playlist ${playlist.name} (${playlist.tracks.size} tracks) to queue")
        manager.scheduler.queue(playlist.tracks)
    }

    override fun noMatches() {
        super.noMatches()

        message.edit("No track(s) found")
    }

    override fun loadFailed(exception: FriendlyException?) {
        super.loadFailed(exception)

        message.edit("Failed to load track(s)")
        LOGGER.error("Failed to load track(s)", exception)
    }
}