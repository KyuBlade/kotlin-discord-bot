package com.omega.discord.bot.audio.loader

import com.omega.discord.bot.audio.TrackUserData
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack


open class AudioLoadHandler : AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack) {
        track.userData = TrackUserData()
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        playlist.tracks.forEach { it.userData = TrackUserData() }
    }

    override fun noMatches() {
    }

    override fun loadFailed(exception: FriendlyException?) {
    }
}