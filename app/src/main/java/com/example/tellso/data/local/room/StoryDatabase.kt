package com.example.tellso.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tellso.data.local.entity.Keys
import com.example.tellso.data.local.entity.Story

@Database(
    entities = [Story::class, Keys::class],
    version = 1,
    exportSchema = false
)

abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun keysDao(): KeysDao
}