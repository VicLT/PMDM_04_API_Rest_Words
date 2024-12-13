package edu.victorlamas.apirestwords.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.flow.Flow

/**
 * Class WordsDataBase.kt
 * Define una base de datos con la tabla Words.
 * @author Víctor Lamas
 */
@Database(entities = [Word::class], version = 1)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordDao(): WordsDao
}

/**
 * Implementaciones del DAO para las clases hijas.
 * @author Víctor Lamas
 */
@Dao
interface WordsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Query("SELECT * FROM Word")
    fun getAllWords(): Flow<List<Word>>

    @Delete
    suspend fun deleteWord(word: Word)
}