package com.omega.discord.bot.ext

import sx.blah.discord.handle.obj.IUser


fun IUser.getNameAndDiscriminator(): String = "$name#$discriminator"
