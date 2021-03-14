package com.token.tokenator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "token_table",
    indices = [Index(value = arrayOf("title"), unique = true)]
)
data class Token(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "token")
    val token: String,

    @ColumnInfo(name = "date_saved")
    val date: String = Date().toString()
)
