package edu.victorlamas.apirestwords.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * DataClass Word.kt
 * Guarda las propiedades de una palabra.
 * @author Víctor Lamas
 */
@Entity
data class Word(
    @SerializedName("idWord")
    @PrimaryKey(autoGenerate = true)
    var idWord: Int = 0,
    @SerializedName("word")
    var word: String? = null,
    @SerializedName("definition")
    var definition: String? = null,
    @Ignore
    var favourite: Boolean = false
)