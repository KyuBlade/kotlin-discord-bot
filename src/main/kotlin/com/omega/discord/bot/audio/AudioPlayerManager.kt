package com.omega.discord.bot.audio

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import sx.blah.discord.handle.obj.IGuild


object AudioPlayerManager {

    private val primaryManager: DefaultAudioPlayerManager = DefaultAudioPlayerManager()
    private val managers: MutableMap<IGuild, GuildAudioPlayerManager> = mutableMapOf()

    init {
        AudioSourceManagers.registerRemoteSources(primaryManager)

        val ytSource: YoutubeAudioSourceManager = primaryManager.source(YoutubeAudioSourceManager::class.java)
        ytSource.setPlaylistPageCount(20)
    }

    fun getAudioManager(guild: IGuild): GuildAudioPlayerManager {
        var audioPlayerManager: GuildAudioPlayerManager? = managers[guild]
        if (audioPlayerManager == null) {
            audioPlayerManager = GuildAudioPlayerManager(primaryManager)
            guild.audioManager.audioProvider = audioPlayerManager.getAudioProvider()
            managers[guild] = audioPlayerManager
        }

        return audioPlayerManager
    }
}