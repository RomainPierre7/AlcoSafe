package com.iseven.alcosafe

import androidx.room.RoomDatabase
import androidx.room.Database

@Database(entities = [Drink::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun drinkDao(): DrinkDAO
}
