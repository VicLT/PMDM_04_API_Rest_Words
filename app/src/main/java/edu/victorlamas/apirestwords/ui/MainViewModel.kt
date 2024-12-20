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
import edu.victorlamas.apirestwords.data.GetWordsUseCase

/**
 * Class MainViewModel.kt
 * Gestiona las operaciones y el estado de los datos en la UI de MainActivity.
 * @author Víctor Lamas
 *
 * @param repository Permite recuperar las palabras y sus estados.
 */
class MainViewModel (private val useCase: GetWordsUseCase, private val repository: WordsRepository) : ViewModel() {
    private var _words: MutableStateFlow<List<Word>> =
        MutableStateFlow(emptyList())
    val words: StateFlow<List<Word>>
        get() = _words.asStateFlow()

    //var favWords: Flow<List<Word>> = MutableStateFlow(emptyList())
    //var favWords: Flow<List<Word>> = repository.getSortedFavWords()
    //var favWords: Flow<List<Word>> = repository.getFavWords()

    /*private var _favWords: MutableStateFlow<List<Word>> =
        MutableStateFlow(emptyList())
    val favWords: StateFlow<List<Word>>
        get() = _favWords.asStateFlow()*/

    init {
        getAllWords("all")
        //getSortedFavWords()
    }

    /**
     * Obtener el listado completo de palabras de la API.
     */
    fun getAllWords(wordsType: String) {
        if (wordsType == "all") {
            viewModelScope.launch {
                useCase().collect { words ->
                    _words.value = words
                }
            }
        } else if (wordsType == "fav") {
            viewModelScope.launch {
                repository.getSortedFavWords().collect { words ->
                    words.forEach { word ->
                        word.favourite = true
                        _words.value = words
                    }
                }
            }
        }
    }
    /*fun getAllWords() {
        viewModelScope.launch {
            repository.getAllApiWords().collect { words ->
                _words.value = words
            }
        }
    }*/

    /*fun getFavWords() {
        viewModelScope.launch {
            repository.getSortedFavWords().collect { words ->
                _words.value = words
        }
    }*/

    /**
     * Filtrar las palabras favoritas por orden alfabético ascendente o descendente.
     */
    /*private fun getSortedFavWords() {
        viewModelScope.launch {
            /*favWords = when (wordsFilter) {
                WordsFilter.ALPHA_ASC -> repository.getAscFavWords()
                WordsFilter.ALPHA_DESC -> repository.getDescFavWords()
            }*/
            //favWords = repository.getSortedFavWords()
            /*repository.getSortedFavWords().collect { words ->
                _favWords.value = words
            }*/
            repository.getFavWords().collect { words ->
                _favWords.value = words
            }
        }
    }*/

    /**
     * Insertar o borrar una palabra favorita en la base de datos local.
     * @param word Id, nombre, descripción y estado favorita.
     */
    fun updateWord(word: Word) {
        viewModelScope.launch {
            repository.updateFavWord(word/*.copy()*/)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val useCase: GetWordsUseCase,
    private val repository: WordsRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(useCase, repository) as T
    }
}