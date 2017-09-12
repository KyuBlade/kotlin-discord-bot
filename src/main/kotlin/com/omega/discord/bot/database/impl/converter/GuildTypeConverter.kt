package com.omega.discord.bot.database.impl.converter

import com.omega.discord.bot.BotManager
import org.mongodb.morphia.converters.SimpleValueConverter
import org.mongodb.morphia.converters.TypeConverter
import org.mongodb.morphia.mapping.MappedField
import sx.blah.discord.handle.impl.obj.Guild
import sx.blah.discord.handle.obj.IGuild


class GuildTypeConverter() : TypeConverter(IGuild::class.java, Guild::class.java), SimpleValueConverter {

    override fun decode(targetClass: Class<*>, fromDBObject: Any, optionalExtraInfo: MappedField): Any {
        return BotManager.client.getGuildByID(fromDBObject as Long)
    }

    override fun encode(value: Any?, optionalExtraInfo: MappedField?): Any? {
        return if (value == null) {
            null
        } else (value as IGuild).longID
    }
}