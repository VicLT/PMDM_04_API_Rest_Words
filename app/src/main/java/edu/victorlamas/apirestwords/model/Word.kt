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
    var idPalabra: Int = 0,
    @SerializedName("palabra")
    var palabra: String? = null,
    @SerializedName("definicion")
    var definicion: String? = null,
    @Ignore
    var favorita: Boolean = false
) {
    constructor(idPalabra: Int, palabra: String?, definicion: String?) :
            this(idPalabra, palabra, definicion, false)
}