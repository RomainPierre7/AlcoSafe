package com.iseven.alcosafe

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drinks_table")
class Drink (
    @PrimaryKey(autoGenerate = true)@NonNull var id: Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "percentage") var percentage: Int,
    @ColumnInfo(name = "quantity") var quantity: Int,
    @ColumnInfo(name = "time") var time: Long,
    )