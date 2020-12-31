package com.token.tokenator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token_table")
data class Token(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "token") val token: String
)