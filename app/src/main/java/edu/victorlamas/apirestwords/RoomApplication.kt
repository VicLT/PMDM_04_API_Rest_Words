package edu.victorlamas.apirestwords

import android.app.Application
import androidx.room.Room
import edu.victorlamas.apirestwords.data.WordsDatabase

/**
 * Clase RoomApplication.kt
 * Inicializa la base de datos en local con Room.
 * @author Víctor Lamas
 */
class RoomApplication : Application() {
    lateinit var wordsDB: WordsDatabase
    private set

    override fun onCreate() {
        super.onCreate()
        wordsDB = Room.databaseBuilder(
            this,
            WordsDatabase::class.java,
            "Words-db"
        ).build()
    }
}