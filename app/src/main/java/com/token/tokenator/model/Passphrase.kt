package com.token.tokenator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passphrase")
data class Passphrase(

    @PrimaryKey()
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "phrase")
    var phrase: String
)
