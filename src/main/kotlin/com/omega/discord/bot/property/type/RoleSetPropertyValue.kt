package com.omega.discord.bot.property.type

import org.mongodb.morphia.annotations.Embedded
import sx.blah.discord.handle.obj.IRole

class RoleSetPropertyValue(value: RoleSetWrapper) : PropertyValue<RoleSetWrapper>(value) {

    constructor() : this(RoleSetWrapper())
}

class RoleSetWrapper {

    @Embedded
    val roleSet: HashSet<IRole> = hashSetOf()
}
