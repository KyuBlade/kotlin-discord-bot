package com.omega.discord.bot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager

class GuildAudioPlayerManager(val manager: AudioPlayerManager) {

    val audioPlayer = manager.createPlayer()!!

    init {
        audioPlayer.volume = 20
    }

    val scheduler: TrackScheduler = TrackScheduler(audioPlayer)

    fun getAudioProvider(): AudioProvider {
        return AudioProvider(audioPlayer)
    }
}