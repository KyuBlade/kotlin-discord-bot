package com.omega.discord.bot.command.impl.config

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.property.GuildProperty
import com.omega.discord.bot.property.GuildPropertyManager
import com.omega.discord.bot.property.type.RoleSetPropertyValue
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.handle.obj.*
import java.util.stream.Collectors


class ManageSelfRoleCommand : Command() {

    override val name: String = "manageSelfRole"
    override val aliases: Array<String>? = arrayOf("msr")
    override val usage: String = "**manageSelfRole add <roleName>** - Add role to the list of self assignable roles\n" +
            "**manageSelfRole remove <roleName>** - Remove the role from the list of self assignable roles"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_MANAGE_SELF_ROLE
    override val ownerOnly: Boolean = false
    override val globalCooldown: Long = 0

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        if (args.size < 2) {

            missingArgs(author, message)
            return
        }

        val roleName = args.drop(1).joinToString(" ")
        val result: List<IRole> = channel.guild.roles.stream()
                .filter { role -> role.name.equals(roleName, true) }
                .collect(Collectors.toList())

        when {
            result.isEmpty() -> MessageSender.sendMessage(channel, "Role $roleName not found")
            result.size > 1 -> MessageSender.sendMessage(channel, "Unable to get role $roleName, several roles were found")
            else -> {

                val role: IRole = result.first()

                when (args[0]) {

                    "add" -> addRole(channel, role)
                    "remove" -> removeRole(channel, role)
                }
            }
        }
    }

    private fun addRole(channel: IChannel, role: IRole) {

        val guild: IGuild = channel.guild

        val propertyValue = GuildPropertyManager.get(guild, GuildProperty.AVAILABLE_SELFROLES) as RoleSetPropertyValue
        val availableSelfRoles = propertyValue.value.roleSet

        availableSelfRoles += role

        GuildPropertyManager.set(guild, GuildProperty.AVAILABLE_SELFROLES, propertyValue)

        MessageSender.sendMessage(channel, "Added role ${role.name} to self role list")
    }

    private fun removeRole(channel: IChannel, role: IRole) {

        val guild: IGuild = channel.guild

        val propertyValue = GuildPropertyManager.get(guild, GuildProperty.AVAILABLE_SELFROLES) as RoleSetPropertyValue
        val availableSelfRoles = propertyValue.value.roleSet

        availableSelfRoles -= role

        GuildPropertyManager.set(guild, GuildProperty.AVAILABLE_SELFROLES, propertyValue)

        MessageSender.sendMessage(channel, "Removed role ${role.name} from self role list")
    }
}