package com.omega.discord.bot.database

import org.bson.types.ObjectId


interface DAO<T> {

    fun find(id: ObjectId): T?

    fun insert(entity: T): T

    fun update(entity: T): T

    fun delete(entity: T)

    fun clean()
}