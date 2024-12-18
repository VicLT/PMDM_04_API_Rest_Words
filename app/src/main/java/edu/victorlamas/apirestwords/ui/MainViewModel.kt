package edu.victorlamas.apirestwords.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.victorlamas.apirestwords.data.WordsRepository
import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Class MainViewModel.kt
 * Gestiona las operaciones y el estado de los datos en la UI de MainActivity.
 * @author Víctor Lamas
 *
 * @param repository Permite recuperar las palabras y sus estados.
 */
class MainViewModel (private val repository: WordsRepository) : ViewModel() {
    private var _words: MutableStateFlow<List<Word>> =
        MutableStateFlow(emptyList())
    val words: StateFlow<List<Word>>
        get() = _words.asStateFlow()

    var favWords: Flow<List<Word>> = repository.getFavWords()

    init {
        getAllWords()
    }

    /**
     * Obtener el listado completo de palabras de la API.
     */
    private fun getAllWords() {
        viewModelScope.launch {
            repository.getAllApiWords().collect { words ->
                _words.value = words
            }
        }
    }

    /**
     * Insertar o borrar una palabra favorita en la base de datos local.
     * @param word Id, nombre, descripción y estado favorita.
     */
    fun updateWord(word: Word) {
        viewModelScope.launch {
            repository.updateFavWord(word)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: WordsRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}