package com.dicoding.asclepius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @field:ColumnInfo(name = "image")
    val image: String,

    @field:ColumnInfo(name = "analysis")
    val analysis: String,

    @field:ColumnInfo(name = "inference")
    val inference: String,

    @field:ColumnInfo(name = "date")
    val date: String
)
