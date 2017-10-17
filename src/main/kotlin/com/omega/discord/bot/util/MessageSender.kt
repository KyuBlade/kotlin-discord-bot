package com.omega.discord.bot.util

import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer


object MessageSender {

    fun sendMessage(channel: IChannel, message: String): Message {

        val managedMessage = Message()

        RequestBuffer.request {

            managedMessage.backedMessage = channel.sendMessage(message)
        }

        return managedMessage
    }

    fun sendMessage(to: IUser, message: String): Message {

        val managedMessage = Message()

        RequestBuffer.request {

            managedMessage.backedMessage = to.orCreatePMChannel.sendMessage(message)
        }

        return managedMessage
    }


    fun sendMessage(channel: IChannel, embedBuilder: EmbedBuilder) {
        RequestBuffer.request {

            channel.sendMessage(embedBuilder.build())
        }
    }

    class Message {

        var backedMessage: IMessage? = null
            set(value) {

                field = value
                delayedQueue.forEach { it.run() }
            }

        private val delayedQueue: ArrayList<Runnable> = arrayListOf()

        fun edit(content: String) {

            if (!isSent()) {

                delayedQueue.add(Runnable {

                    backedMessage!!.edit(content)
                })
            } else {

                backedMessage!!.edit(content)
            }
        }

        fun isSent() = backedMessage != null
    }
}