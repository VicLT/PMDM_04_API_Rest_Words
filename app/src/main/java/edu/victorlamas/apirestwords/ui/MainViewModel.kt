package edu.victorlamas.apirestwords.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.victorlamas.apirestwords.data.WordsRepository
import edu.victorlamas.apirestwords.model.Palabras
import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Class MainViewModel.kt
 * Gestiona las operaciones y el estado de los datos en la UI de MainActivity
 * @author Víctor Lamas
 *
 * @param repository Permite recuperar las palabras y sus estados
 */
class MainViewModel (private val repository: WordsRepository) : ViewModel() {
    private var _words = repository.getWords() as StateFlow<List<Word>>
    val words: StateFlow<List<Word>>
        get() = _words

    /**
     * Obtener el listado completo de palabras de la API.
     * @author Víctor Lamas
     */
    fun getAllWords(): Flow<List<Palabras>> {
        return repository.fetchWords()
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: WordsRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}