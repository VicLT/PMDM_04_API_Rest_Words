package edu.victorlamas.apirestwords.data

import edu.victorlamas.apirestwords.model.Palabras
import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

/**
 * Class WordsRepository.kt
 * Repositorio que actúa como intermediario entre el ViewModel y WordsDataSource.
 * @author Víctor Lamas
 *
 * @param remoteDataSource Datos heredados de la clase RemoteDataSource.
 * @param localDataSource Datos heredados de la clase LocalDataSource.
 */
class WordsRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {
    fun fetchWords(): Flow<Palabras> {
        return remoteDataSource.fetchWords()
    }

    /**
     * Guarda las propiedades de una palabra y bloquea el hilo hasta que termine.
     * @author Víctor Lamas
     *
     * @param word Id, nombre, descripción y estado de favorito.
     */
    fun saveWord(word: Word) = runBlocking {
        localDataSource.insertWord(word)
        delay(10)
    }

    fun getWords(): StateFlow<List<Word>> {
        return localDataSource.getAllWords()
    }

    fun getWordById(idWord: Int): Flow<Word?> {
        return localDataSource.getWordById(idWord)
    }
}