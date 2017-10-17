package com.omega.discord.bot.database

import com.omega.discord.bot.property.GuildProperties
import com.omega.discord.bot.property.GuildProperty
import com.omega.discord.bot.property.type.PropertyValue
import sx.blah.discord.handle.obj.IGuild


interface GuildPropertiesDAO : DAO<GuildProperties> {

    fun findFor(guild: IGuild): GuildProperties?

    fun updateProperty(entity: GuildProperties, property: GuildProperty, value: PropertyValue<*>): GuildProperties
}