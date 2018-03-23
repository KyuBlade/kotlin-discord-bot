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

    override val name: String = "nsfw"
    override val aliases: Array<String>? = null
    override val usage: String =
            "**__NSFW Commands are only available in an NSFW channel__**\n\n" +
            "**nsfw list** - Get list of available nsfw categories\n" +
                    "**nsfw <categoryName>** - Get a random image for the provided category"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_NSFW
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 1

    val CATEGORIES: Array<String> = arrayOf("boobs", "butts")

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        message.delete()

        if(!channel.isNSFW) {

            MessageSender.sendMessage(author, "Command unavailable outside of an NSFW channel")

        } else if (args.isEmpty()) { // No arguments provided

            missingArgs(author, message)

        } else {

            when (args.first()) {

                "list" -> listCategories(channel)
                "hentai" -> sendHentai(channel)
                "boobs" -> sendBoobOrButt(channel, BoobAndButtService.ImageType.BOOBS)
                "butts" -> sendBoobOrButt(channel, BoobAndButtService.ImageType.BUTTS)
            }
        }
    }

    private fun listCategories(channel: IChannel) {

        val stringBuilder = StringBuilder("```\n")

        val categoryCount = CATEGORIES.size

        CATEGORIES.forEach { stringBuilder.append(it).append('\n') }

        stringBuilder.append("```")

        MessageSender.sendMessage(channel, stringBuilder.toString())
    }

    private fun sendHentai(channel: IChannel) {

    }

    private fun sendBoobOrButt(channel: IChannel, imageType: BoobAndButtService.ImageType) {

        BoobAndButtService.getRandomImage(imageType, object : BoobAndButtService.ResultCallback {

            override fun onFailure(e: IOException) {

                MessageSender.sendMessage(channel, "Request failed")
            }

            override fun onResult(imageUrl: String) {

                MessageSender.sendMessage(channel, imageUrl)
            }
        })
    }
}