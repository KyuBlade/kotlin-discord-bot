package com.omega.discord.bot.database.impl.converter

import com.omega.discord.bot.BotManager
import org.mongodb.morphia.converters.SimpleValueConverter
import org.mongodb.morphia.converters.TypeConverter
import org.mongodb.morphia.mapping.MappedField
import sx.blah.discord.handle.impl.obj.User
import sx.blah.discord.handle.obj.IUser


class UserTypeConverter : TypeConverter(IUser::class.java, User::class.java), SimpleValueConverter {

    override fun decode(targetClass: Class<*>, fromDBObject: Any?, optionalExtraInfo: MappedField): Any? {
        return if (fromDBObject == null) {
            null
        } else BotManager.client.fetchUser(fromDBObject as Long)

    }

    override fun encode(value: Any?, optionalExtraInfo: MappedField?): Any? {
        return if (value == null) {
            null
        } else (value as IUser).longID

    }
}