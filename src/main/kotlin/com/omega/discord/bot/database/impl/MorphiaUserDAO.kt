package com.omega.discord.bot.database.impl

import com.omega.discord.bot.database.UserDAO
import com.omega.discord.bot.permission.Permission
import com.omega.discord.bot.permission.PermissionOverride
import com.omega.discord.bot.permission.User
import org.bson.types.ObjectId
import org.mongodb.morphia.Datastore
import sx.blah.discord.handle.obj.IGuild


class MorphiaUserDAO(private val datastore: Datastore) : UserDAO {

    override fun find(id: ObjectId): User? =
            datastore.get(User::class.java, id)

    override fun findFor(guild: IGuild): List<User> =
            datastore.createQuery(User::class.java)
                    .field("guild").equal(guild)
                    .asList()

    override fun insert(entity: User): User {
        datastore.save(entity)

        return entity
    }

    override fun update(entity: User): User {
        datastore.save(entity)

        return entity
    }

    override fun updatePermission(entity: User, permission: Permission, override: PermissionOverride) {
        datastore.update(entity,
                datastore.createUpdateOperations(User::class.java)
                        .set("permissions." + permission.name, override))
    }

    override fun updateGroup(entity: User) {
        datastore.update(entity,
                datastore.createUpdateOperations(User::class.java)
                        .set("group", entity.group)
        )
    }

    override fun delete(entity: User) {
        datastore.delete(entity)
    }

    override fun clean() {
    }
}