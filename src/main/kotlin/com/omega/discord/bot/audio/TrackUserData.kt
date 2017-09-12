package com.omega.discord.bot.audio

import sx.blah.discord.handle.obj.IUser


data class TrackUserData(val skipVotes: MutableSet<IUser> = mutableSetOf())