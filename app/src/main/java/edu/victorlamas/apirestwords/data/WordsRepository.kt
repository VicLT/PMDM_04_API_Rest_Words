package edu.victorlamas.apirestwords.data

import edu.victorlamas.apirestwords.model.Word
import edu.victorlamas.apirestwords.utils.WordsFilter
import edu.victorlamas.apirestwords.utils.wordsFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.forEach

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
    /*suspend fun getAllApiWords(): Flow<List<Word>> {
        val allWords: Flow<List<Word>> = remoteDataSource.getAllApiWords()
        val favWords: Flow<List<Word>> = localDataSource.getFavWordsAsc()
        val allWordsWithCurrentFav: Flow<List<Word>> = MutableStateFlow(emptyList())
        allWords.collect { allWord ->
            favWords.collect { favWord ->
                allWord.forEach { word ->
                    favWord.forEach { fav ->
                        if (word.word == fav.word) {
                            word.favourite = true
                        }
                    }
                }
            }
        }
        return allWordsWithCurrentFav
    }*/

    fun getAllApiWords(): Flow<List<Word>> {
        return remoteDataSource.getAllApiWords()
    }

    fun getSortedFavWords(): Flow<List<Word>> {
        return when (wordsFilter) {
            WordsFilter.ALPHA_ASC -> localDataSource.getFavWordsAsc()
            WordsFilter.ALPHA_DESC -> localDataSource.getFavWordsDesc()
        }
    }

    /*fun getFavWords(): Flow<List<Word>> {
        return localDataSource.getFavWordsAsc()
    }*/

    /*fun getAscFavWords(): Flow<List<Word>> {
        return localDataSource.getFavWordsAsc()
    }

    fun getDescFavWords(): Flow<List<Word>> {
        return localDataSource.getFavWordsDesc()
    }*/

    suspend fun updateFavWord(favWord: Word) {
        if (favWord.favourite) {
            localDataSource.saveFavWord(favWord)
        } else {
            localDataSource.deleteFavWord(favWord)
        }
    }
}