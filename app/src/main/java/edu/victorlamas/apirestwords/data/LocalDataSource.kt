package edu.victorlamas.apirestwords.data

import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.flow.Flow

/**
 * Class LocalDataSource.kt
 * Maneja los datos de la BD local con Room.
 * @author Víctor Lamas
 *
 * @param db Funciones heredadas de WordsDataBase.
 */
class LocalDataSource(private val db: WordsDao) {
    /**
     * Inserta una palabra favorita en la BD local.
     * @param word Palabra marcada como favorita.
     */
    suspend fun saveFavWord(word: Word) {
        db.saveFavWord(word)
    }

    /**
     * Consigue la lista de palabras favoritas de la BD local en orden ascendente.
     * @return Flujo frío de lista de palabras favoritas ordenadas.
     */
    fun getFavWordsAsc(): Flow<List<Word>> {
        return db.getFavWordsAsc()
    }

    /**
     * Consigue la lista de palabras favoritas de la BD local en orden descendente.
     * @return Flujo frío de lista de palabras favoritas ordenadas.
     */
    fun getFavWordsDesc(): Flow<List<Word>> {
        return db.getFavWordsDesc()
    }

    /**
     * Elimina una palabra favorita de la BD local.
     * @param word Palabra marcada como favorita.
     */
    suspend fun deleteFavWord(word: Word) {
        db.deleteFavWord(word)
    }
}