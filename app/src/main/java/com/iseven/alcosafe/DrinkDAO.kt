package com.iseven.alcosafe

import androidx.room.*

@Dao
interface DrinkDAO {
    @Insert
    fun insertDrink(drink: Drink)

    @Query("SELECT * FROM drinks_table ORDER BY time DESC")
    fun getAllDrinks(): List<Drink>

    @Query("SELECT * FROM drinks_table where id=:id")
    fun getDrink(id: Int): Drink

    @Update
    fun updateDrink(drink: Drink)

    @Delete
    fun deleteDrink(drink: Drink)

    @Query("DELETE FROM drinks_table")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM drinks_table")
    fun count(): Int

    @Query("SELECT id FROM drinks_table ORDER BY time DESC LIMIT 1")
    fun lastDrink(): Int
}
