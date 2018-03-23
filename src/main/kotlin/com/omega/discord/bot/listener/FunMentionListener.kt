package com.omega.discord.bot.listener

import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent


class FunMentionListener {

    @EventSubscriber
    fun onMessage(event: MentionEvent) {

        val messageContent: String = event.message.content

        val reply: String = when {

            messageContent.trimEnd().endsWith("❤") -> "${event.author.mention()} ❤"
            else -> ""
        }

        if (!reply.isBlank())
            MessageSender.sendMessage(event.channel, reply)
    }
}