package com.omega.discord.bot.database

import com.omega.discord.bot.permission.Group
import com.omega.discord.bot.permission.Permission
import sx.blah.discord.handle.obj.IGuild


interface GroupDAO : DAO<Group> {

    fun findFor(guild: IGuild): List<Group>

    fun addPermission(entity: Group, permission: Permission)

    fun removePermission(entity: Group, permission: Permission)
}