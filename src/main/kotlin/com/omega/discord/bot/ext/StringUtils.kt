package com.omega.discord.bot.ext

import com.omega.discord.bot.BotManager
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.handle.obj.IUser

class StringUtils {

    companion object {

        private val USER_MENTION_PATTERN: Regex = Regex("<@!?(\\d*)>")
        private val ROLE_MENTION_PATTERN: Regex = Regex("<@&+(\\d*)>")

        /**
         * Format timestamp to a string.
         *
         * @param timestamp timestamp in milliseconds
         * @return a formatted string of the timestamp in format HH:mm:ss
         */
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

        /**
         * Parse a duration string to milliseconds.
         *
         * @param duration duration string in format HH:mm:ss
         * @return duration timestamp in milliseconds
         * @throws NumberFormatException if the string is malformed
         */
        fun parseDuration(stringToParse: String): Long {
            val split = stringToParse.split(":").asReversed()
            var hours: Long = 0
            var minutes: Long = 0
            var seconds: Long = 0

            for ((index, value) in split.withIndex()) {

                when (index) {
                    0 -> seconds = value.toLong()
                    1 -> minutes = value.toLong()
                    2 -> hours = value.toLong()
                }
            }

            return ((hours * 60L * 60L) + (minutes * 60L) + seconds) * 1000L
        }

        /**
         * Create an ASCII progress bar of form [###---] for a duration progress.
         * @param position the position in the duration
         * @param duration the duration
         * @param stepCount number of step the progress bar will draw. A step represent a # or a - (Default to 30)
         * @return the drawn progress bar
         */
        fun getTrackAsciiProgressBar(position: Long, duration: Long, stepCount: Int = 30): String {
            val progress: Double = position.toDouble() / duration.toDouble()
            val builder = StringBuilder()

            with(builder) {

                append('[')
                for (i in 1..stepCount) {
                    if (progress > i.toFloat() / stepCount.toFloat()) {
                        append('#')
                    } else {
                        append('-')
                    }
                }
                append(']')
            }

            return builder.toString()
        }

        /**
         * Parse a discord user mention to a User object.
         * @param mentionStr the mention string
         * @return The user object or null if not found
         * @throws NumberFormatException if the mention string is malformed
         */
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

        /**
         * Check if the string is a role mention
         *
         * @param mentionStr the string to test
         * @return true if the string is a role mention, false otherwise
         */
        fun isRoleMention(mentionStr: String): Boolean =
                mentionStr.matches(ROLE_MENTION_PATTERN)

        /**
         * Parse a discord role mention to a Role object.
         *
         * @param mentionStr the mention string
         * @return The role object or null if not found
         * @throws NumberFormatException if the mention string is malformed
         */
        fun parseRoleMention(mentionStr: String): IRole? {

            val result: MatchResult? = ROLE_MENTION_PATTERN.find(mentionStr)

            return try {
                val id: Long? = result?.groupValues?.get(1)?.toLong()
                BotManager.client.getRoleByID(id!!)
            } catch (e: NumberFormatException) {
                println(e)
                null
            }
        }

        fun sanitizeMentions(data: String): String {

            var sanitizedData:String = data

            USER_MENTION_PATTERN.findAll(data).forEach {

                val user: IUser? = try {

                    it.groups[1]?.value?.toLong()?.let {

                        BotManager.client.getUserByID(it)
                    }
                } catch (e: NumberFormatException) {

                    null
                }

                val userName: String = user?.getNameAndDiscriminator() ?: "unknown user"
                sanitizedData = sanitizedData.replaceRange(it.range, userName)
            }

            ROLE_MENTION_PATTERN.findAll(data).forEach {

                val role: IRole? = try {

                    it.groups[1]?.value?.toLong()?.let {

                        BotManager.client.getRoleByID(it)
                    }
                } catch (e: NumberFormatException) {

                    null
                }

                val roleName: String = role?.name ?: "unknown role"
                sanitizedData = sanitizedData.replaceRange(it.range, roleName)
            }

            return sanitizedData
        }
    }
}

