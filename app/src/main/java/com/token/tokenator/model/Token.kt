package com.token.tokenator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "token_table",
    indices = [Index(value = arrayOf("title"), unique = true)]
)
data class Token(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "token")
    var token: String,

    @ColumnInfo(name = "login")
    var login: String? = null,

    @ColumnInfo(name = "date_saved")
    var date: String = Date().toString()
)
