package com.omega.discord.bot.command.impl.config

import com.omega.discord.bot.BotManager
import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.property.GuildProperty
import com.omega.discord.bot.property.GuildPropertyManager
import com.omega.discord.bot.property.type.RolePropertyValue
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent
import sx.blah.discord.handle.obj.*
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.PermissionUtils
import sx.blah.discord.util.RequestBuffer


class AutoRoleCommand : Command {

    init {
        BotManager.client.dispatcher.registerListener(this)
    }

    override val name: String = "autorole"
    override val aliases: Array<String>? = arrayOf("ar")
    override val usage: String = "**autorole <roleMention>** - Automatically assign the mentioned role when a user join the server\n" +
            "**autorole <roleName>** - Automatically assign the named role when a user join the server\n" +
            "**autorole none** - To don't assign any role when users join the server"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_AUTOROLE
    override val ownerOnly: Boolean = false

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {

        when {
            args.isEmpty() -> missingArgs(author, message)

            args.first().equals("none", true) -> { // none parameter

                if (GuildPropertyManager.set(channel.guild, GuildProperty.AUTOROLE, RolePropertyValue()))
                    MessageSender.sendMessage(channel, "No role will be automatically assigned to new members")
                else
                    MessageSender.sendMessage(channel, "Unable to set autorole")
            }

            StringUtils.isRoleMention(args.first()) -> { // It's a mentioned role

                val role = StringUtils.parseRoleMention(args.first())
                role?.let {

                    setAutorole(channel, role)
                }
            }

            else -> { // It's a role name

                val roleName = args.joinToString(" ")
                val foundRoles: MutableList<IRole> = channel.guild.getRolesByName(roleName)

                if (foundRoles.isNotEmpty()) {

                    val firstRole = foundRoles.first()

                    setAutorole(channel, firstRole)
                } else {

                    MessageSender.sendMessage(channel, "Unable to find role with name $roleName")
                }
            }
        }
    }

    private fun setAutorole(channel: IChannel, role: IRole) {

        if (GuildPropertyManager.set(channel.guild, GuildProperty.AUTOROLE, RolePropertyValue(role)))

            MessageSender.sendMessage(channel, "New members will automatically be assigned to role ${role.name}" +
                    if (!PermissionUtils.hasPermissions(channel.guild, channel.client.ourUser, Permissions.MANAGE_ROLES))
                        "\nYou will need to set permission to manage roles on the bot to work"
                    else
                        ""
            )
        else
            MessageSender.sendMessage(channel, "Unable to set autorole")
    }

    @EventSubscriber
    fun onUserJoinCreate(event: UserJoinEvent) {

        val autoroleProperty = GuildPropertyManager.get(event.guild, GuildProperty.AUTOROLE) as RolePropertyValue

        autoroleProperty.value?.let {

            RequestBuffer.request {

                try {
                    event.user.addRole(it)
                } catch (e: MissingPermissionsException) {
                    MessageSender.sendMessage(event.guild.owner, "${e.errorMessage}\nUnable to assign role automatically")
                }
            }
        }
    }
}