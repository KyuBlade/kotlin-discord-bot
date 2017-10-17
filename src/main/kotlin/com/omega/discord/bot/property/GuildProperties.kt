package com.omega.discord.bot.property

import com.omega.discord.bot.property.type.PropertyValue
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import sx.blah.discord.handle.obj.IGuild

@Entity("guild_properties")
class GuildProperties() {

    @Id
    val id: ObjectId? = null

    @Embedded("guild_id")
    lateinit var guild: IGuild

    @Embedded
    private val properties: MutableMap<GuildProperty, in PropertyValue<*>?> = mutableMapOf()

    constructor(guild: IGuild) : this() {

        this.guild = guild
    }

    fun get(property: GuildProperty): PropertyValue<*> = properties[property] as PropertyValue<*>

    fun <T : PropertyValue<*>> set(property: GuildProperty, value: T) {
        properties[property] = value
    }
}