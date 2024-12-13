package edu.victorlamas.apirestwords.data

import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

/**
 * Class LocalDataSource.kt
 * Maneja los datos con Room.
 * @author Víctor Lamas
 *
 * @param db Funciones heredadas de WordsDataBase.
 */
class LocalDataSource(private val db: WordDao) {
    suspend fun insertWord(word: Word) {
        db.insertWord(word)
    }

    fun getAllWords(): StateFlow<List<Word>> {
        return db.getAllWords()
    }

    fun getWordById(id: Int) = flow {
        emit(db.getWordByIdWord(id))
    }
}