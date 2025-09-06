package com.example.myapptranclatesky.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val query: String = "",
    val translation: String,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)