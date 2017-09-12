package com.omega.discord.bot.command.impl.permission

import com.omega.discord.bot.command.Command
import com.omega.discord.bot.ext.StringUtils
import com.omega.discord.bot.ext.getNameAndDiscriminator
import com.omega.discord.bot.permission.*
import com.omega.discord.bot.util.MessageSender
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser


class PermissionsCommand : Command {

    override val name: String = "permissions"
    override val aliases: Array<String>? = arrayOf("perms")
    override val usage: String = "**permissions list** - Get the list of available permissions\n" +
            "**permissions list <userMention|groupName>** - Get the list of permissions for the mentioned user\n" +
            "**permissions addpermission <permissionName> <userMention|groupName>** - Add a permission to the mentioned user or a group\n" +
            "**permissions removepermission <permissionName> <userMention|groupName>** - Remove a permission from the mentioned user or a group\n" +
            "**permissions groups** - Get the list of available groups\n" +
            "**permissions addgroup <groupName>** - Add a new group\n" +
            "**permissions removegroup <groupName>** - Remove a group\n" +
            "**permissions setgroup <groupName> <userMention>** - Set a group to the mentioned user"
    override val allowPrivate: Boolean = false
    override val permission: Permission? = Permission.COMMAND_PERMISSIONS
    override val ownerOnly: Boolean = false

    override fun execute(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {
        if (args.isNotEmpty()) {
            val action: String = args[0]
            when (action) {
                "list" -> permissionList(author, channel, args)
                "addpermission" -> editPermission(author, channel, message, true, args)
                "removepermission" -> editPermission(author, channel, message, false, args)
                "groups" -> groupList(author, channel)
                "addgroup" -> editGroup(author, channel, message, true, args)
                "removegroup" -> editGroup(author, channel, message, false, args)
                "setgroup" -> setGroup(author, channel, message, args)
                "test" -> testPerm(channel, args)
                else -> MessageSender.sendMessage(author,
                        "Action $action for command permissions not found\n\nUsage:\n\n$usage")
            }
        } else {
            missingArgs(author, message)
        }
    }

    private fun testPerm(channel: IChannel, args: List<String>) {
        val user = StringUtils.parseUserMention(args[2])
        val permission = Permission.get(args[1])

        if (user != null && permission != null)
            MessageSender.sendMessage(channel, "" + PermissionManager.hasPermission(channel.guild, user, permission))
    }

    private fun permissionList(author: IUser, channel: IChannel, args: List<String>) {
        if (args.size < 2) { // All available permissions

            sendPermissionList(author, "Available permissions", Permission.values().asIterable())
        } else { // Permissions for the mentioned user or group

            val target = args[1]
            if (target.startsWith("<@")) { // It's a user

                val user: IUser? = StringUtils.parseUserMention(target)
                if (user != null) {
                    val permissions: MutableSet<Permission> = mutableSetOf()

                    val userPerms: User = PermissionManager.getPermissions(channel.guild, user)
                    val group = userPerms.group

                    permissions.addAll(group.permissions)
                    userPerms.permissions.forEach {
                        if (it.value == PermissionOverride.REMOVE)
                            permissions.remove(it.key)
                        else if (it.value == PermissionOverride.ADD)
                            permissions.add(it.key)
                    }

                    sendPermissionList(author, "Permissions for user ${user.getNameAndDiscriminator()}", permissions.asIterable())
                } else {
                    MessageSender.sendMessage(channel, "User not found")
                }
            } else { // It's a group
                val permissions = PermissionManager.getPermissions(channel.guild, target)?.permissions
                if (permissions != null)
                    sendPermissionList(author, "Permissions for group $target", permissions.asIterable())
                else
                    MessageSender.sendMessage(channel, "Group $target not found")
            }
        }
    }

    private fun sendPermissionList(author: IUser, title: String, iterablePerms: Iterable<Permission>) {
        val builder = StringBuilder("```$title :\n\n")

        iterablePerms.forEach { builder.append(it.key).append("\n") }
        builder.append("```")

        MessageSender.sendMessage(author, builder.toString())
    }

    private fun editPermission(author: IUser, channel: IChannel, message: IMessage, add: Boolean, args: List<String>) {
        val guild: IGuild = channel.guild

        if (args.size >= 3) {

            val permissionKey = args[1]
            val permission: Permission? = Permission.get(permissionKey)
            val target = args[2]

            if (permission == null) {
                MessageSender.sendMessage(channel, "Permission $permissionKey not found")
            } else if (target.startsWith("<@")) { // It's a user

                val user: IUser? = StringUtils.parseUserMention(target)
                if (user != null) {
                    if (add)
                        PermissionManager.setPermission(guild, user, permission, PermissionOverride.ADD)
                    else
                        PermissionManager.setPermission(guild, user, permission, PermissionOverride.REMOVE)

                    MessageSender.sendMessage(channel,
                            "Permission $permissionKey ${if (add) "added to" else "removed from"} user ${user.mention()}")
                } else {
                    MessageSender.sendMessage(channel, "User not found")
                }
            } else { // It's a group

                val success: Boolean =
                        if (add) PermissionManager.addPermission(guild, target, permission)
                        else PermissionManager.removePermission(guild, target, permission)

                if (success)
                    MessageSender.sendMessage(channel,
                            "Permission $permissionKey ${if (add) "added to" else "removed from"} group $target")
                else
                    MessageSender.sendMessage(channel, "Group $target not found")
            }
        } else { // Not enough parameters
            missingArgs(author, message)
        }
    }

    private fun groupList(author: IUser, channel: IChannel) {
        val guild: IGuild = channel.guild
        val groups: Collection<Group> = PermissionManager.getGroups(guild)

        val builder = StringBuilder("```Available groups for guild ${guild.name}:\n\n")

        with(builder) {
            groups.forEach { append(it.name).append("\r\n") }
            append("```")

            MessageSender.sendMessage(author, toString())
        }
    }

    private fun editGroup(author: IUser, channel: IChannel, message: IMessage, add: Boolean, args: List<String>) {
        val guild = channel.guild

        if (args.size >= 2) {
            val groupName = args[1].toLowerCase()

            if (add && PermissionManager.addGroup(guild, groupName) == null)
                MessageSender.sendMessage(channel, "Group $groupName already exist")
            else if (!add && !PermissionManager.removeGroup(guild, groupName))
                MessageSender.sendMessage(channel, "Group $groupName doesn't exist")
            else
                MessageSender.sendMessage(channel, "${if (add) "Added" else "Removed"} group $groupName")
        } else {
            missingArgs(author, message)
        }
    }

    private fun setGroup(author: IUser, channel: IChannel, message: IMessage, args: List<String>) {
        if (args.size >= 3) {
            val groupName = args[1].toLowerCase()
            val user: IUser? = StringUtils.parseUserMention(args[2])

            if (user != null) {
                if (PermissionManager.setGroup(channel.guild, user, groupName))
                    MessageSender.sendMessage(channel, "Set group $groupName to user ${user.mention()}")
                else
                    MessageSender.sendMessage(channel, "Group $groupName not found")
            } else {
                MessageSender.sendMessage(channel, "User not found")
            }
        } else {
            missingArgs(author, message)
        }
    }
}