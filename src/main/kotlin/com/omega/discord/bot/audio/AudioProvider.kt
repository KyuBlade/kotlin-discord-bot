package com.omega.discord.bot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import sx.blah.discord.handle.audio.AudioEncodingType
import sx.blah.discord.handle.audio.IAudioProvider




class AudioProvider(val audioPlayer: AudioPlayer) : IAudioProvider {

    private var lastFrame: AudioFrame? = null

    override fun isReady(): Boolean {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide()
        }

        return lastFrame != null
    }

    override fun provide(): ByteArray {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide()
        }

        val data: ByteArray = lastFrame?.data!!
        lastFrame = null

        return data
    }

    override fun getAudioEncodingType(): AudioEncodingType {
        return AudioEncodingType.OPUS
    }
}