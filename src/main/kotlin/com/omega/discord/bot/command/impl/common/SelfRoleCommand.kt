package com.omega.discord.bot.command.impl.common

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.property.GuildProperty
import com.omega.discord.bot.property.GuildPropertyManager
import com.omega.discord.bot.property.type.RoleSetWrapper
import com.omega.discord.bot.service.MessageSender
import sx.blah.discord.handle.obj.*
import sx.blah.discord.util.MissingPermissionsException
import java.util.stream.Collectors
import kotlin.streams.toList


class SelfRoleCommand : Command() {

    override val name: String = "selfrole"
    override val aliases: Array<String>? = arrayOf("sr")
    override val usage: String = "**selfrole list** - Get the list of available roles\n" +
            "**selfrole list <role_name>** - Get the users having the provided role" +
            "**selfrole add <role_name>** - Add the role to yourself\n" +
            "**selfrole remove <role_name>** - Remove the role from yourself"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_SELF_ROLE
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 0

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        if (args.isEmpty()) {

            missingArgs(author, message)
            return
        }

        when (args[0]) {

            "list" -> {
                if (args.size > 1) {

                    val roleName = StringUtils.sanitizeMentions(args.drop(1).joinToString(" "))
                    val role: IRole? = channel.guild.roles.firstOrNull { it.name.equals(roleName, true) }

                    if(role == null)
                        MessageSender.sendMessage(channel, "Role $roleName not found")
                    else
                        listUsers(channel, role)

                } else
                    listRoles(channel)
            }
            else -> {

                if (args.size < 2) {

                    missingArgs(author, message)
                    return
                }

                val roleName = StringUtils.sanitizeMentions(args.drop(1).joinToString(" "))
                val result: List<IRole> = channel.guild.roles.stream().filter { role -> role.name.equals(roleName, true) }
                        .collect(Collectors.toList())

                when {
                    result.isEmpty() -> MessageSender.sendMessage(channel, "Role $roleName not found")
                    result.size > 1 -> MessageSender.sendMessage(channel, "Unable to get role $roleName, several roles were found")
                    else -> {

                        val guild: IGuild = channel.guild
                        val wrapper: RoleSetWrapper = GuildPropertyManager.get(guild, GuildProperty.AVAILABLE_SELFROLES).value as RoleSetWrapper
                        val availableRoles: MutableSet<IRole> = wrapper.roleSet
                        val role: IRole = result.first()

                        if (!availableRoles.contains(role)) {

                            MessageSender.sendMessage(channel, "Role ${role.name} is not a self assignable role")

                        } else {

                            when (args[0]) {

                                "add" -> addRole(channel, role, author)
                                "remove" -> removeRole(channel, role, author)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun listRoles(channel: IChannel) {

        val guild: IGuild = channel.guild
        val wrapper: RoleSetWrapper = GuildPropertyManager.get(guild, GuildProperty.AVAILABLE_SELFROLES).value as RoleSetWrapper
        val availableRoles: MutableSet<IRole> = wrapper.roleSet

        val builder = StringBuilder()

        if (availableRoles.isEmpty()) {

            builder.append("**No role available**")
        } else {

            builder.append("**Available roles:**\n\n")

            val rolesStr = availableRoles.stream()
                    .map { (it as IRole).name }
                    .toList()
                    .joinToString("\n")

            builder.append(rolesStr)
        }

        MessageSender.sendMessage(channel, builder.toString())
    }

    private fun listUsers(channel: IChannel, role: IRole) {

        val users: List<IUser> = channel.guild.getUsersByRole(role)
        val builder = StringBuilder()

        if (users.isEmpty()) {

            builder.append("**No users found**")
        } else {

            builder.append("**Users for role ${role.name}:**\n\n")

            val userStr = users.stream()
                    .map { (it as IUser).name}
                    .toList()
                    .joinToString("\n")

            builder.append(userStr)
        }

        MessageSender.sendMessage(channel, builder.toString())
    }

    private fun addRole(channel: IChannel, role: IRole, user: IUser) {

        try {
            user.addRole(role)
            MessageSender.sendMessage(channel, "Role ${role.name} added")
        } catch (e: MissingPermissionsException) {

            MessageSender.sendMessage(channel, "Permission: ${e.missingPermissions} needed")
        }
    }

    private fun removeRole(channel: IChannel, role: IRole, user: IUser) {

        try {
            user.removeRole(role)
            MessageSender.sendMessage(channel, "Role ${role.name} removed")
        } catch (e: MissingPermissionsException) {

            MessageSender.sendMessage(channel, "Permission: ${e.missingPermissions} needed")
        }
    }
}