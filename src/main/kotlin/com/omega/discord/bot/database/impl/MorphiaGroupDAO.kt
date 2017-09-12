package com.omega.discord.bot.database.impl

import com.omega.discord.bot.database.GroupDAO
import com.omega.discord.bot.permission.Group
import com.omega.discord.bot.permission.Permission
import org.bson.types.ObjectId
import org.mongodb.morphia.Datastore
import sx.blah.discord.handle.obj.IGuild


class MorphiaGroupDAO(private val datastore: Datastore) : GroupDAO {

    override fun find(id: ObjectId): Group? =
            datastore.get(Group::class.java, id)

    override fun findFor(guild: IGuild): List<Group> =
            datastore.createQuery(Group::class.java)
                    .field("guild").equal(guild)
                    .asList()

    override fun insert(entity: Group): Group {
        datastore.save(entity)

        return entity
    }

    override fun update(entity: Group): Group {
        datastore.save(entity)

        return entity
    }

    override fun addPermission(entity: Group, permission: Permission) {
        datastore.update(entity,
                datastore.createUpdateOperations(Group::class.java)
                        .addToSet("permissions", permission)
        )
    }

    override fun removePermission(entity: Group, permission: Permission) {
        datastore.update(entity,
                datastore.createUpdateOperations(Group::class.java)
                        .removeAll("permissions", listOf(permission))
        )
    }

    override fun delete(entity: Group) {
        datastore.delete(entity)
    }

    override fun clean() {
    }
}