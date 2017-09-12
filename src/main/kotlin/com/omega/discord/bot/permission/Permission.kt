package com.omega.discord.bot.permission

enum class Permission(val key: String) {

    COMMAND_QUEUE("command.queue"),
    COMMAND_JOIN("command.join"),
    COMMAND_LEAVE("command.leave"),
    COMMAND_SKIP("command.skip"),
    COMMAND_SKIP_FORCE("command.skip.force"),
    COMMAND_SKIP_MULTIPLE("command.skip.multiple"),
    COMMAND_INVITE("command.invite"),
    COMMAND_PERMISSIONS("command.permissions"),
    COMMAND_KICK("command.kick"),
    COMMAND_BAN("command.ban");


    companion object {
        fun get(key: String): Permission? = values().find { it.key.contentEquals(key) }
    }
}