package edu.victorlamas.apirestwords.data

import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.flow.Flow

/**
 * Class LocalDataSource.kt
 * Maneja los datos con Room.
 * @author Víctor Lamas
 *
 * @param db Funciones heredadas de WordsDataBase.
 */
class LocalDataSource(private val db: WordsDao) {
    suspend fun saveFavWord(word: Word) {
        db.saveFavWord(word)
    }

    fun getFavWordsAsc(): Flow<List<Word>> {
        return db.getFavWordsAsc()
    }

    fun getFavWordsDesc(): Flow<List<Word>> {
        return db.getFavWordsDesc()
    }

    suspend fun deleteFavWord(word: Word) {
        db.deleteFavWord(word)
    }
}