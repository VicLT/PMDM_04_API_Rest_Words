package edu.victorlamas.apirestwords.data

import edu.victorlamas.apirestwords.model.Word
import edu.victorlamas.apirestwords.utils.WordsFilter
import kotlinx.coroutines.flow.Flow

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
    fun getAllApiWords(): Flow<List<Word>> {
        return remoteDataSource.getAllApiWords()
    }

    fun getSortedFavWords(filter: WordsFilter): Flow<List<Word>> {
        return when (filter) {
            WordsFilter.ALPHA_ASC -> localDataSource.getFavWordsAsc()
            WordsFilter.ALPHA_DESC -> localDataSource.getFavWordsDesc()
        }
    }

    suspend fun updateFavWord(favWord: Word) {
        if (favWord.favourite) {
            localDataSource.saveFavWord(favWord)
        } else {
            localDataSource.deleteFavWord(favWord)
        }
    }
}