package edu.victorlamas.apirestwords.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.victorlamas.apirestwords.data.WordsRepository
import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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

    init {
        getAllWords()
    }

    /**
     * Obtener el listado completo de palabras de la API.
     */
    private fun getAllWords() {
        viewModelScope.launch {
            combine(repository.fetchWords(), repository.getWords()) { remoteWords, localWords ->
                remoteWords.forEach { remoteWord ->
                    val wordAux = localWords.find { localWord ->
                        remoteWord.idWord == localWord.idWord
                    }
                    remoteWord.favourite = wordAux != null
                }
                _words.value = remoteWords
            }.collect()
        }
    }
    /*private fun getAllWords() {
        viewModelScope.launch {
            repository.fetchWords().collect { words ->
                _words.value = words
            }
        }
    }*/

    /**
     * Guardar una palabra en la base de datos local.
     * @param word Id, nombre y descripción.
     */
    fun saveWord(word: Word) {
        viewModelScope.launch {
            repository.saveWord(word)
        }
    }

    /**
     * Eliminar una palabra en la base de datos local.
     * @param word Id, nombre y descripción.
     */
    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.deleteWord(word)
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