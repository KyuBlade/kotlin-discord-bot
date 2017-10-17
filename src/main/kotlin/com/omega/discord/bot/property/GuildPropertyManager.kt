package com.omega.discord.bot.property

import com.omega.discord.bot.BotManager
import com.omega.discord.bot.database.DatabaseFactory
import com.omega.discord.bot.property.type.PropertyValue
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent
import sx.blah.discord.handle.obj.IGuild


object GuildPropertyManager {

    init {
        BotManager.client.dispatcher.registerListener(this)
    }

    private val guildPropertiesMap: MutableMap<IGuild, GuildProperties> = hashMapOf()

    /**
     * Get a property value for a guild.
     *
     * @param guild witch guild to get property from
     * @param property the property to get value of
     * @return the property value
     */
    fun get(guild: IGuild, property: GuildProperty): PropertyValue<*> =
            guildPropertiesMap[guild]!!.get(property)

    /**
     * Set a property for a guild.
     *
     * @param guild witch guild to set property to
     * @param property the property to set
     * @param value the property value to set
     * @return true if value has been set successfully, false otherwise
     */
    fun <T : PropertyValue<*>> set(guild: IGuild, property: GuildProperty, value: T): Boolean {

        guildPropertiesMap[guild]?.let {

            it.set(property, value)
            DatabaseFactory.guildPropertiesDAO.updateProperty(it, property, value)

            return true
        }

        return false
    }

    @EventSubscriber
    fun onGuildCreate(event: GuildCreateEvent) {

        val guild = event.guild

        var guildProperties = DatabaseFactory.guildPropertiesDAO.findFor(guild)
        if (guildProperties == null) {

            guildProperties = GuildProperties(guild)

            GuildProperty.values().forEach {

                guildProperties!!.set(it, it.defaultValue)
            }

            DatabaseFactory.guildPropertiesDAO.insert(guildProperties)
        }

        guildPropertiesMap[guild] = guildProperties
    }
}