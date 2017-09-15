package com.omega.discord.bot.ext

import com.omega.discord.bot.NotSeekableException
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer


/**
 * Seek to the given position in the playing track.
 *
 * @param position time in milliseconds to seek to (eg: 60000 will seek to one minute)
 * @throws IllegalStateException if no track is playing
 * @throws NotSeekableException if the track is not seekable
 */
fun AudioPlayer.seek(position: Long) {
    val currentTrack = playingTrack

    if (currentTrack != null) {

        if (!currentTrack.isSeekable)
            throw NotSeekableException()

        currentTrack.position = position
    } else
        throw IllegalStateException("No track playing")
}