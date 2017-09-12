package com.omega.discord.bot.command

import com.omega.discord.bot.command.impl.HelpCommand
import com.omega.discord.bot.command.impl.InviteCommand
import com.omega.discord.bot.command.impl.moderation.BanCommand
import com.omega.discord.bot.command.impl.moderation.KickCommand
import com.omega.discord.bot.command.impl.music.JoinCommand
import com.omega.discord.bot.command.impl.music.LeaveCommand
import com.omega.discord.bot.command.impl.music.QueueCommand
import com.omega.discord.bot.command.impl.music.SkipCommand
import com.omega.discord.bot.command.impl.permission.PermissionsCommand


object CommandRegistry {

    private val commands: MutableMap<String, Command> = hashMapOf()
    private val aliases: MutableMap<String, Command> = hashMapOf()

    init {
        register(HelpCommand())
        register(JoinCommand())
        register(LeaveCommand())
        register(QueueCommand())
        register(SkipCommand())
        register(InviteCommand())
        register(PermissionsCommand())
        register(KickCommand())
        register(BanCommand())
    }

    fun register(command: Command) {
        commands.putIfAbsent(command.name, command)

        command.aliases?.forEach { aliases.putIfAbsent(it, command) }
    }

    fun unregister(command: Command) = commands.remove(command.name)

    fun get(commandName: String): Command? = commands[commandName] ?: aliases[commandName]

    fun get(): MutableCollection<Command> = commands.values
}