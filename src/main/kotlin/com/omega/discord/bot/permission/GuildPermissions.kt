package com.omega.discord.bot.permission

import com.omega.discord.bot.database.DatabaseFactory
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser


class GuildPermissions(val guild: IGuild, groups: List<Group> = listOf(), users: List<User> = listOf()) {

    private val userMap: MutableMap<IUser, User> = hashMapOf()
    private val groupMap: MutableMap<String, Group> = hashMapOf()

    private val defaultGroup: Group

    init {
        // First init
        if (groups.isEmpty()) {
            defaultGroup = Group(guild = guild, name = "default",
                    permissions = hashSetOf(
                            Permission.COMMAND_INVITE, Permission.COMMAND_SKIP, Permission.COMMAND_QUEUE
                    )
            )

            groupMap["default"] = defaultGroup

            DatabaseFactory.groupDAO.insert(defaultGroup)
        } else {
            groups.forEach {
                groupMap[it.name] = it
            }
            users.forEach {
                userMap[it.user] = it
            }

            defaultGroup = groupMap["default"]!!
        }
    }

    /**
     * Change a permission for a user
     * @param user the user to set permission for
     * @param permission the permission to change
     * @param override the permission override to set for this permission
     */
    fun set(user: IUser, permission: Permission, override: PermissionOverride) =
            getUserPermissions(user).set(permission, override)

    /**
     * Get permissions of a user.
     * @param user user to get permissions for
     */
    fun get(user: IUser): User = getUserPermissions(user)

    /**
     * Add permissions to a group.
     * @param groupName name of the group
     * @param permission permission to add
     * @return the group if permission has been added, null if the group was not found
     */
    fun add(groupName: String, permission: Permission): Group? {
        val group: Group? = getGroupPermissions(groupName)

        group?.add(permission)

        return group
    }

    /**
     * Add permissions to a group.
     * @param groupName name of the group
     * @param permission permission to remove
     * @return the group if permission has been added, null if the group was not found
     */
    fun remove(groupName: String, permission: Permission): Group? {
        val group: Group? = getGroupPermissions(groupName)

        group?.remove(permission)

        return group
    }

    /**
     * Get permissions of a group.
     * @param groupName group to get permissions for
     */
    fun get(groupName: String): Group? = getGroupPermissions(groupName)

    /**
     * Add a group.
     * @param name name of the new group
     * @return the created group, null if it already exists
     */
    fun addGroup(name: String): Group? {
        return if (name !in groupMap) {
            val group = Group(guild = guild, name = name)
            groupMap[name] = group

            group
        } else {
            null
        }
    }

    /**
     * Remove a group.
     * @param name name of the group to remove
     * @return the removed group, null if it doesn't exists
     */
    fun removeGroup(name: String): Group? {
        return if (name in groupMap) {
            groupMap.remove(name)
        } else {
            null
        }
    }

    /**
     * Get the list of groups.
     */
    fun getGroups(): Collection<Group> = groupMap.values

    /**
     * Set the group of a user.
     * @param user user to set group to
     * @param name the group to set
     * @return the user if the group has been set, null if the group was not found
     */
    fun setGroup(user: IUser, name: String): User? {
        val group: Group? = getGroupPermissions(name)
        return if (group != null) {
            val userPerm = getUserPermissions(user)
            userPerm.group = group

            userPerm
        } else {
            null
        }
    }

    /**
     * Check if the user have the permission.
     * @param user user to check
     * @param permission permission to check
     */
    fun hasPermission(user: IUser, permission: Permission): Boolean =
            getUserPermissions(user).has(permission)

    private fun getUserPermissions(user: IUser): User {
        var userPerms = userMap[user]
        if (userPerms == null) {
            userPerms = User(guild = guild, group = defaultGroup, user = user)
            userMap[user] = userPerms

            DatabaseFactory.userDAO.insert(userPerms)
        }

        return userPerms
    }

    /**
     * Get a group.
     * @param name the group name to get
     * @return the group associated with this name or null if not found
     */
    private fun getGroupPermissions(name: String): Group? = groupMap[name]
}