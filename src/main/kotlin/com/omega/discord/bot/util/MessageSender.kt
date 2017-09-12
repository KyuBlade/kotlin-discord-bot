package com.omega.discord.bot.util

import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer


object MessageSender {

    fun sendMessage(channel: IChannel, message: String) {
        RequestBuffer.request {
            channel.sendMessage(message)
        }
    }

    fun sendMessage(to: IUser, message: String) {
        RequestBuffer.request {
            to.orCreatePMChannel.sendMessage(message)
        }
    }
}