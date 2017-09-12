package com.omega.discord.bot.database

import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.permission.PermissionOverride
import com.omega.discord.bot.permission.User
import sx.blah.discord.handle.obj.IGuild


interface UserDAO : DAO<User> {

    fun findFor(guild: IGuild): List<User>

    fun updatePermission(entity: User, permission: Permission, override: PermissionOverride)

    fun updateGroup(entity: User)
}