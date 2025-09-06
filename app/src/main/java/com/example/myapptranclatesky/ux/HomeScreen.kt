package com.example.myapptranclatesky.ux

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapptranclatesky.data.local.TranslationEntity
import androidx.compose.foundation.text.KeyboardActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    vm: MainViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Переводчик (Skyeng)") },
                actions = {
                    TextButton(onClick = { navController.navigate(Destinations.FAVORITES) }) {
                        Text("Избранное")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // поле для ввода слова
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::updateQuery,
                label = { Text("Введите слово на английском") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { vm.translate() })
            )

            Spacer(Modifier.height(8.dp))

            // кнопка перевода
            Button(
                onClick = vm::translate,
                enabled = !state.isLoading && state.query.isNotBlank()
            ) {
                Text(if (state.isLoading) "Загрузка..." else "Перевести")
            }

            // вывод ошибки
            state.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Text(err, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            // список истории переводов
            Text("История", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            HistoryList(
                items = state.history,
                onFavorite = vm::toggleFavorite,
                onDelete = vm::delete
            )
        }
    }
}

@Composable
private fun HistoryList(
    items: List<TranslationEntity>,
    onFavorite: (TranslationEntity) -> Unit,
    onDelete: (TranslationEntity) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { item ->
            Card(
                Modifier
                    .fillMaxWidth()
                    ) {
                Row(
                    Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(item.word, style = MaterialTheme.typography.titleMedium)
                        Text(item.translation, style = MaterialTheme.typography.bodyMedium)
                    }
                    Row {
                        // избранное
                        IconButton(onClick = { onFavorite(item) }) {
                            Icon(
                                imageVector = if (item.isFavorite)
                                    Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,contentDescription = null
                            )
                        }
                        // удаление
                        IconButton(onClick = { onDelete(item) }) {
                            Icon(Icons.Filled.Delete, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}