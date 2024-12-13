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
    suspend fun insertWord(word: Word) {
        db.insertWord(word)
    }

    fun getAllWords(): Flow<List<Word>> {
        return db.getAllWords()
    }

    suspend fun deleteWord(word: Word) {
        db.deleteWord(word)
    }
}