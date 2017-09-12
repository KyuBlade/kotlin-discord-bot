package com.omega.discord.bot.database.impl.converter

import com.omega.discord.bot.BotManager
import org.mongodb.morphia.converters.SimpleValueConverter
import org.mongodb.morphia.converters.TypeConverter
import org.mongodb.morphia.mapping.MappedField
import sx.blah.discord.handle.impl.obj.Channel
import sx.blah.discord.handle.obj.IChannel


class ChannelTypeConverter : TypeConverter(IChannel::class.java, Channel::class.java), SimpleValueConverter {

    override fun decode(targetClass: Class<*>, fromDBObject: Any, optionalExtraInfo: MappedField): Any {
        return BotManager.client.getChannelByID(fromDBObject as Long)
    }

    override fun encode(value: Any?, optionalExtraInfo: MappedField?): Any? {
        return if (value == null) {
            null
        } else (value as IChannel).longID
    }
}