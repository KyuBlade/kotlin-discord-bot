package com.omega.discord.bot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.*

class TrackScheduler(private val audioPlayer: AudioPlayer) : AudioEventAdapter() {

    init {
        audioPlayer.addListener(this)
    }

    private val queue: Deque<AudioTrack> = ArrayDeque()

    fun queue(track: AudioTrack) {
        if (!audioPlayer.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    fun queue(tracks: List<AudioTrack>) {
        for (track in tracks) {
            queue(track)
        }
    }

    fun getQueuedTracks(range: IntRange): List<AudioTrack> {
        val list: MutableList<AudioTrack> = mutableListOf()

        val finalRange: IntRange = if (range.endInclusive > queue.size - 1)
            range.start until queue.size
        else
            range

        if (queue.size > 0)
            finalRange.mapTo(list) { queue.elementAt(it) }

        return list
    }

    private fun nextTrack() = audioPlayer.startTrack(queue.poll(), false)

    /**
     * Skip the currently playing track and remove count - 1, or count - 2 if no track is playing, from the queue
     * @return the number of tracks removed from the queue
     */
    fun skip(count: Int): Int {

        val currentTrack = audioPlayer.playingTrack
        val minusCount = if (currentTrack != null) 0 else 1

        val queueSize = queue.size
        val finalCount = Math.min(queueSize, count - minusCount)

        queue.removeAll(getQueuedTracks(0 until finalCount))
        nextTrack()

        return finalCount
    }

    /**
     * @return the number of tracks in queue
     */
    fun queueSize(): Int = queue.size

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) {
            nextTrack()
        }
    }
}