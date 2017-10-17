package com.omega.discord.bot.database.impl

import com.omega.discord.bot.database.GuildPropertiesDAO
import com.omega.discord.bot.property.GuildProperties
import com.omega.discord.bot.property.GuildProperty
import com.omega.discord.bot.property.type.PropertyValue
import org.bson.types.ObjectId
import org.mongodb.morphia.Datastore
import sx.blah.discord.handle.obj.IGuild


class MorphiaGuildPropertiesDAO(private val datastore: Datastore) : GuildPropertiesDAO {

    override fun find(id: ObjectId): GuildProperties? =
            datastore.get(GuildProperties::class.java, id)

    override fun findFor(guild: IGuild): GuildProperties? =
            datastore.createQuery(GuildProperties::class.java)
                    .field("guild").equal(guild)
                    .get()

    override fun insert(entity: GuildProperties): GuildProperties {
        datastore.save(entity)

        return entity
    }

    override fun update(entity: GuildProperties): GuildProperties {
        datastore.save(entity)

        return entity
    }

    override fun updateProperty(entity: GuildProperties, property: GuildProperty, value: PropertyValue<*>): GuildProperties {
        datastore.update(entity,
                datastore.createUpdateOperations(GuildProperties::class.java)
                        .set("properties.${property.name}", value))

        return entity
    }

    override fun delete(entity: GuildProperties) {
        datastore.delete(entity)
    }

    override fun clean() {
    }
}