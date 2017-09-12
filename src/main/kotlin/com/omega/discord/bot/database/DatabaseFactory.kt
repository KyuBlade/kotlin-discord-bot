package com.omega.discord.bot.database

import com.mongodb.MongoClient
import com.omega.discord.bot.database.impl.MorphiaGroupDAO
import com.omega.discord.bot.database.impl.MorphiaUserDAO
import com.omega.discord.bot.database.impl.converter.ChannelTypeConverter
import com.omega.discord.bot.database.impl.converter.GuildTypeConverter
import com.omega.discord.bot.database.impl.converter.RoleTypeConverter
import com.omega.discord.bot.database.impl.converter.UserTypeConverter
import com.omega.discord.bot.permission.Group
import com.omega.discord.bot.permission.User
import org.mongodb.morphia.Morphia


object DatabaseFactory {

    private val morphia = Morphia()
    private val datastore = morphia.createDatastore(MongoClient(), "kotlin_bot")

    init {
        morphia.map(setOf(
                User::class.java,
                Group::class.java
        ))

        with(morphia.mapper.converters) {
            addConverter(GuildTypeConverter())
            addConverter(ChannelTypeConverter())
            addConverter(UserTypeConverter())
            addConverter(RoleTypeConverter())
        }

        datastore.ensureIndexes()
    }


    val userDAO: UserDAO = MorphiaUserDAO(datastore)
    val groupDAO: GroupDAO = MorphiaGroupDAO(datastore)


    fun close() {
        userDAO.clean()
        groupDAO.clean()
    }
}