package com.omega.discord.bot.property.type

import sx.blah.discord.handle.obj.IRole


class RolePropertyValue(value: IRole?) : PropertyValue<IRole?>(value) {

    constructor() : this(null)
}