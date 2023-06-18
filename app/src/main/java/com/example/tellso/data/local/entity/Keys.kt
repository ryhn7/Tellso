package com.example.tellso.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keys")
data class Keys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
