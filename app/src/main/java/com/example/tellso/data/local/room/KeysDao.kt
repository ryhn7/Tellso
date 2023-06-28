package com.example.tellso.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tellso.domain.entity.Keys

@Dao
interface KeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(key: List<Keys>)

    @Query("SELECT * FROM keys WHERE id = :id")
    suspend fun getKeysId(id: String): Keys?

    @Query("DELETE FROM keys")
    suspend fun deleteKeys()
}