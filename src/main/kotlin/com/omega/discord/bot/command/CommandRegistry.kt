package com.omega.discord.bot.command

import com.omega.discord.bot.command.impl.common.HelpCommand
import com.omega.discord.bot.command.impl.common.InviteCommand
import com.omega.discord.bot.command.impl.common.RollCommand
import com.omega.discord.bot.command.impl.common.SelfRoleCommand
import com.omega.discord.bot.command.impl.config.AutoRoleCommand
import com.omega.discord.bot.command.impl.config.ManageSelfRoleCommand
import com.omega.discord.bot.command.impl.config.PrefixCommand
import com.omega.discord.bot.command.impl.moderation.BanCommand
import com.omega.discord.bot.command.impl.moderation.KickCommand
import com.omega.discord.bot.command.impl.music.*
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
        register(SeekCommand())
        register(PrefixCommand())
        register(AutoRoleCommand())
        register(SelfRoleCommand())
        register(ManageSelfRoleCommand())
        register(RollCommand())
    }

    fun register(command: Command) {
        commands.putIfAbsent(command.name, command)

        command.aliases?.forEach { aliases.putIfAbsent(it, command) }
    }

    fun unregister(command: Command) = commands.remove(command.name)

    fun get(commandName: String): Command? = commands[commandName] ?: aliases[commandName]

    fun get(): MutableCollection<Command> = commands.values
}