package com.omega.discord.bot.property.type

import sx.blah.discord.handle.obj.IChannel


class ChannelPropertyValue(value: IChannel?) : PropertyValue<IChannel?>(value) {

    constructor() : this(null)
}