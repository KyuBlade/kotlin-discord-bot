package com.omega.discord.bot.command.impl.common

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import java.util.concurrent.ThreadLocalRandom

private const val MAX_ROLL_COUNT: Short = 50

class RollCommand : Command() {

    override val name: String = "roll"
    override val aliases: Array<String>? = null
    override val usage: String = "**roll <rollLiteral>** - Get a random number after x rolls. Format : <count>d<range> or <count>D<range>\n" +
            "**roll <count> <range>** - Get a random number after x rolls"
    override val allowPrivate: Boolean = true
    override val permission: Permission? = Permission.COMMAND_ROLL
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 0

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        if (args.isEmpty()) {

            missingArgs(author, message)

        } else if (args.size == 1) {

            val split = args.first().split("[dD]".toRegex())

            if (split.size < 2) {

                MessageSender.sendMessage(channel, "Malformed literal")
            } else {

                try {
                    val count = split[0].toInt()
                    val range = split[1].toInt()

                    roll(channel, count, range)

                } catch (e: NumberFormatException) {

                    MessageSender.sendMessage(channel, "Malformed literal")
                }
            }
        } else {

            try {
                val count = args[0].toInt()
                val range = args[1].toInt()

                roll(channel, count, range)

            } catch (e: NumberFormatException) {

                MessageSender.sendMessage(channel, "Malformed literal")
            }
        }
    }

    private fun roll(channel: IChannel, count: Int, range: Int) {

        var result = 0L

        if (count > MAX_ROLL_COUNT)
            MessageSender.sendMessage(channel, "You can't roll more than $MAX_ROLL_COUNT times")
        else if (count <= 0)
            MessageSender.sendMessage(channel, "Roll count must be higher than 0")
        else if (range <= 0)
            MessageSender.sendMessage(channel, "Roll range must be higher than 0")
        else {

            val builder = StringBuilder(" (")
            val longRange = range.toLong()

            for (i in 1..count) {

                val next = ThreadLocalRandom.current().nextLong(longRange + 1)

                result += next

                if (result < 0) {

                    MessageSender.sendMessage(channel, "Result is too long")
                    break
                }

                builder.append(next)

                if (i < count)
                    builder.append(" + ")
            }

            if (result >= 0) {

                builder.append(')')
                MessageSender.sendMessage(channel, "Roll gave $result $builder")
            }
        }
    }
}