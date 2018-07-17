package com.omega.discord.bot.command.impl.nsfw

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.service.MessageSender
import com.omega.discord.bot.service.nsfw.BoobAndButtService
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import java.io.IOException


class NSFWCommand : Command() {

    private val maxCount = 5;

    override val name: String = "nsfw"
    override val aliases: Array<String>? = null
    override val usage: String =
            "**__NSFW Commands are only available in an NSFW channel__**\n\n" +
                    "**nsfw list** - Get list of available nsfw categories\n" +
                    "**nsfw <categoryName>** - Get a random image for the provided category\n" +
                    "**nsfw <categoryName> <count>** - Get a defined number of random images for the provided category. The count parameter must be between 1 and $maxCount"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_NSFW
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 1

    val CATEGORIES: Array<String> = arrayOf("boobs", "butts")

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        message.delete()

        if (!channel.isNSFW) {

            MessageSender.sendMessage(author, "Command unavailable outside of an NSFW channel")

        } else if (args.isEmpty()) { // No arguments provided

            missingArgs(author, message)

        } else {

            val count: Int =
                    if (args.size <= 1) 1 // Return 1 if the parameter is not provided
                    else
                        try { // Return the parsed number
                            args[1].toInt()
                        } catch (e: NumberFormatException) { // Return 1 if the count parameter is not a number
                            1
                        }.let {
                            // Check that the count is between the limits, return the closest limit otherwise
                            if (it < 1) 1 else if (it > maxCount) maxCount else it
                        }

            when (args.first()) {

                "list" -> listCategories(channel)
                "hentai" -> sendHentai(channel)
                "boobs" -> sendBoobOrButt(channel, BoobAndButtService.ImageType.BOOBS, count)
                "butts" -> sendBoobOrButt(channel, BoobAndButtService.ImageType.BUTTS, count)
            }
        }
    }

    private fun listCategories(channel: IChannel) {

        val stringBuilder = StringBuilder("```\n")

        CATEGORIES.forEach { stringBuilder.append(it).append('\n') }
        stringBuilder.append("```")

        MessageSender.sendMessage(channel, stringBuilder.toString())
    }

    private fun sendHentai(channel: IChannel) {

    }

    private fun sendBoobOrButt(channel: IChannel, imageType: BoobAndButtService.ImageType, count: Int) {

        BoobAndButtService.getRandomImages(imageType, count, object : BoobAndButtService.ResultCallback {

            override fun onFailure(e: IOException) {

                MessageSender.sendMessage(channel, "Request failed")
            }

            override fun onResult(results: List<BoobAndButtService.NSFWResult>) {

                val stringBuilder = StringBuilder()

                results.forEachIndexed({ i, result ->

                    result.modelName?.let { stringBuilder.append("Model: ").append(it).append('\n') }
                    stringBuilder.append(result.imageUrl)

                    if (i < results.size - 1)
                        stringBuilder.append("\n\n")
                })

                MessageSender.sendMessage(channel, stringBuilder.toString())
            }
        })
    }
}