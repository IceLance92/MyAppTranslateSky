package com.example.myapptranclatesky.data

import com.example.myapptranclatesky.data.local.TranslationDao
import com.example.myapptranclatesky.data.local.TranslationEntity
import com.example.myapptranclatesky.data.remote.SkyengService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TranslationRepository(
    private val api: SkyengService,
    private val dao: TranslationDao
) {
    fun observeHistory(): Flow<List<TranslationEntity>> = dao.observeAll()
    fun observeFavorites(): Flow<List<TranslationEntity>> = dao.observeFavorites()

    suspend fun translateAndSave(word: String): Result<TranslationEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.search(word)
            val translation = response.firstOrNull()
                ?.meanings?.firstOrNull()?.translation?.text
                ?: throw IllegalStateException("NO_TRANSLATION")

            val entity = TranslationEntity(word = word, translation = translation)
            val id = dao.insert(entity)
            entity.copy(id = id)
        }
    }

    suspend fun toggleFavorite(id: Long, newValue: Boolean) = dao.setFavorite(id, newValue)
    suspend fun delete(id: Long) = dao.deleteById(id)
}