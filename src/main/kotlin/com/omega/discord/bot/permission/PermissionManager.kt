package com.omega.discord.bot.permission

import com.omega.discord.bot.BotManager
import com.omega.discord.bot.database.DatabaseFactory
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser


object PermissionManager {

    init {
        BotManager.client.dispatcher.registerListener(this)
    }

    private val permissions: MutableMap<IGuild, GuildPermissions> = hashMapOf()

    fun setPermission(guild: IGuild, user: IUser, permission: Permission, override: PermissionOverride) {
        val userPerms = getUserPermissions(guild, user)

        userPerms.set(permission, override)

        if (userPerms.id == null)
            DatabaseFactory.userDAO.insert(userPerms)
        else
            DatabaseFactory.userDAO.updatePermission(userPerms, permission, override)
    }

    /**
     * Get permissions of a user.
     * @param guild the targeted guild
     * @param user user to get permissions for
     */
    fun getPermissions(guild: IGuild, user: IUser) = getUserPermissions(guild, user)

    /**
     * Add permissions to a group.
     * @param guild the targeted guild
     * @param groupName name of the group
     * @param permission permission to add
     * @return true if permission has been added, false if the group was not found
     */
    fun addPermission(guild: IGuild, groupName: String, permission: Permission): Boolean {
        val group = getGuildPermissions(guild).add(groupName.toLowerCase(), permission)

        if (group != null)
            DatabaseFactory.groupDAO.addPermission(group, permission)

        return group != null
    }

    /**
     * Add permissions to a group.
     * @param guild the targeted guild
     * @param groupName name of the group
     * @param permission permission to remove
     * @return true if permission has been added, false if the group was not found
     */
    fun removePermission(guild: IGuild, groupName: String, permission: Permission): Boolean {
        val group = getGuildPermissions(guild).remove(groupName.toLowerCase(), permission)

        if (group != null)
            DatabaseFactory.groupDAO.removePermission(group, permission)

        return group != null
    }

    /**
     * Get permissions of a group.
     * @param guild the targeted guild
     * @param groupName group to get permissions for
     */
    fun getPermissions(guild: IGuild, groupName: String): Group? =
            getGuildPermissions(guild).get(groupName.toLowerCase())

    /**
     * Add a group.
     * @param guild the targeted guild
     * @param name name of the new group
     * @return @return the created group, null if it already exists
     */
    fun addGroup(guild: IGuild, name: String): Group? {
        val group = getGuildPermissions(guild).addGroup(name.toLowerCase())

        if (group != null)
            DatabaseFactory.groupDAO.insert(group)

        return group
    }

    /**
     * Remove a group.
     * @param guild the targeted guild
     * @param name name of the group to remove
     * @return true if group has been removed, false otherwise
     */
    fun removeGroup(guild: IGuild, name: String): Boolean {
        val group = getGuildPermissions(guild).removeGroup(name.toLowerCase())

        if (group != null)
            DatabaseFactory.groupDAO.delete(group)

        return group != null
    }

    /**
     * Get the list of groups
     * @param guild the targeted guild
     */
    fun getGroups(guild: IGuild): Collection<Group> = getGuildPermissions(guild).getGroups()

    /**
     * Set the group of a user.
     * @param guild the targeted guild
     * @param user user to set group to
     * @param groupName the group to set
     * @return true if group set successfully, false otherwise
     */
    fun setGroup(guild: IGuild, user: IUser, groupName: String): Boolean {
        val userPerm = getGuildPermissions(guild).setGroup(user, groupName.toLowerCase())

        if (userPerm != null)
            DatabaseFactory.userDAO.updateGroup(userPerm)

        return userPerm != null
    }

    fun hasPermission(guild: IGuild, user: IUser, permission: Permission): Boolean =
            getGuildPermissions(guild).hasPermission(user, permission)

    private fun getGuildPermissions(guild: IGuild): GuildPermissions =
            permissions[guild] ?: throw IllegalAccessException("Permissions for guild ${guild.name} not found")

    private fun getUserPermissions(guild: IGuild, user: IUser): User = getGuildPermissions(guild).get(user)

    @EventSubscriber
    fun onGuildCreate(event: GuildCreateEvent) {
        val guild = event.guild

        val groups = DatabaseFactory.groupDAO.findFor(guild)
        val users = DatabaseFactory.userDAO.findFor(guild)

        permissions[event.guild] = GuildPermissions(guild, groups, users)
        println("Added permissions or guild ${guild.name} - ${permissions.size}")
    }

    @EventSubscriber
    fun onGuildLeave(event: GuildLeaveEvent) {
        println("Removing permissions for guild ${event.guild.name}")
        permissions.remove(event.guild)
    }
}