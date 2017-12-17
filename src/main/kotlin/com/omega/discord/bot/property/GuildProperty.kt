package com.omega.discord.bot.property

import com.omega.discord.bot.property.type.PropertyValue
import com.omega.discord.bot.property.type.RolePropertyValue
import com.omega.discord.bot.property.type.RoleSetPropertyValue
import com.omega.discord.bot.property.type.StringPropertyValue


enum class GuildProperty(val key: String, val defaultValue: PropertyValue<*>) {

    COMMAND_PREFIX("command.prefix", StringPropertyValue("!")),
    AVAILABLE_SELFROLES("available.selfroles", RoleSetPropertyValue()),
    AUTOROLE("autorole", RolePropertyValue());

    companion object {
        fun get(key: String): GuildProperty? = GuildProperty.values().find { it.key.contentEquals(key) }
    }
}