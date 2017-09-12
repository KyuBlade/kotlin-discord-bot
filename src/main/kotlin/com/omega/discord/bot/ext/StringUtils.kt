package com.omega.discord.bot.ext

import com.omega.discord.bot.BotManager
import sx.blah.discord.handle.obj.IUser

class StringUtils {

    companion object {

        private val USER_MENTION_PATTERN: Regex = Regex("<@!?(\\d*)>")

        fun formatDuration(timestamp: Long): String {
            var remaining: Long = timestamp
            val hours: Long = remaining / 3600000L
            remaining -= hours * 3600000L

            val minutes: Long = remaining / 60000L
            remaining -= minutes * 60000L

            val seconds: Long = remaining / 1000L

            val builder = StringBuilder()
            with(builder) {
                // Hours
                if (hours > 0) {
                    if (hours < 10)
                        append('0')

                    append("$hours:")
                }

                // Minutes
                if (minutes < 10)
                    append('0')

                append("$minutes:")

                // Seconds
                if (seconds < 10)
                    append('0')

                append(seconds)

                return toString()
            }
        }

        fun getTrackAsciiProgressBar(position: Long, duration: Long): String {
            val stepCount = 30
            val progress: Double = position.toDouble() / duration.toDouble()
            val builder = StringBuilder()

            for (i in 1..stepCount) {
                if (progress  > i.toFloat() / stepCount.toFloat()) {
                    builder.append('#')
                } else {
                    builder.append('-')
                }
            }

            return builder.toString()
        }

        fun parseUserMention(mentionStr: String): IUser? {
            val result: MatchResult? = USER_MENTION_PATTERN.find(mentionStr)
            return try {
                val id: Long? = result?.groupValues?.get(1)?.toLong()
                BotManager.client.getUserByID(id!!)
            } catch (e: NumberFormatException) {
                println(e)
                null
            }
        }
    }
}

