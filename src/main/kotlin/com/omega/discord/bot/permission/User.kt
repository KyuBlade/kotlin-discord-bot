package com.omega.discord.bot.permission

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Reference
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

@Entity("users")
class User() {

    @Id
    var id: ObjectId? = null

    @Embedded("guild_id")
    lateinit var guild: IGuild


    @Reference
    lateinit var group: Group

    @Embedded("user_id")
    lateinit var user: IUser

    @Embedded
    val permissions: MutableMap<Permission, PermissionOverride> = mutableMapOf()

    constructor(guild: IGuild, group: Group, user: IUser) : this() {
        this.guild = guild
        this.group = group
        this.user = user
    }

    /**
     * Change a permission for this user
     * @param permission the permission to change
     * @param override the permission override to set for this permission
     */
    fun set(permission: Permission, override: PermissionOverride) {
        if (override == PermissionOverride.INHERIT) {
            permissions.remove(permission)
        } else {
            permissions.put(permission, override)
        }
    }

    /**
     * Check if the user has the permission
     * @param permission permission to check for
     * @return true if he have permission, false otherwise
     */
    fun has(permission: Permission): Boolean {
        return group.contains(permission) && permissions[permission] != PermissionOverride.REMOVE ||
                permissions[permission] == PermissionOverride.ADD
    }
}