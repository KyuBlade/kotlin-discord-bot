package com.omega.discord.bot.permission

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import sx.blah.discord.handle.obj.IGuild

@Entity("groups")
class Group() {

    @Id
    var id: ObjectId? = null

    @Embedded("guild_id")
    lateinit var guild: IGuild
        private set

    lateinit var name: String

    @Embedded
    val permissions: MutableSet<Permission> = hashSetOf()

    constructor(guild: IGuild, name: String, permissions: MutableSet<Permission> = hashSetOf()) : this() {
        this.guild = guild
        this.name = name
        this.permissions.addAll(permissions)
    }

    /**
     * Add a permission to this group
     * @param permission the permission to add
     * @return true if the permission is already set, false otherwise
     */
    fun add(permission: Permission): Boolean = permissions.add(permission)

    /**
     * Remove a permission from this group
     * @param permission the permission to remove
     * @return true if the permission has been removed, false if the permission was not already set
     */
    fun remove(permission: Permission): Boolean = permissions.remove(permission)

    /**
     * Check if this group have the permission.
     * @return true if this group have the permission, false otherwise
     */
    fun contains(permission: Permission): Boolean = permissions.contains(permission)
}