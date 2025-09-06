package com.example.myapptranclatesky.ux

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapptranclatesky.data.TranslationRepository
import com.example.myapptranclatesky.data.local.TranslationEntity
import com.example.myapptranclatesky.di.ServiceLocator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val history: List<TranslationEntity> = emptyList(),
    val favorites: List<TranslationEntity> = emptyList()
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo: TranslationRepository = ServiceLocator.provideRepository(app)

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeHistory().collect { list ->
                _state.update { it.copy(history = list) }
            }
        }
        viewModelScope.launch {
            repo.observeFavorites().collect { list ->
                _state.update { it.copy(favorites = list) }
            }
        }
    }

    fun updateQuery(q: String) { _state.update { it.copy(query = q) } }

    fun translate() {
        val word = state.value.query
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repo.translateAndSave(word)
            _state.update { currentState ->
                result.fold(
                    onSuccess = { translationEntity -> currentState.copy(isLoading = false, query = "") },
                    onFailure = { err ->
                        currentState.copy(isLoading = false, error = userMessage(err))
                    }
                )
            }
        }
    }

    private fun userMessage(t: Throwable): String = when (t.message) {
        "EMPTY" -> "Введите слово"
        "NO_TRANSLATION" -> "Перевод не найден"
        else -> "Ошибка сети"
    }

    fun toggleFavorite(item: TranslationEntity) {
        viewModelScope.launch { repo.toggleFavorite(item.id, !item.isFavorite) }
    }

    fun delete(item: TranslationEntity) {
        viewModelScope.launch { repo.delete(item.id) }
    }
}