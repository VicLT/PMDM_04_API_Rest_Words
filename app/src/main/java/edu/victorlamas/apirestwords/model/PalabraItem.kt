package edu.victorlamas.apirestwords.model

import com.google.gson.annotations.SerializedName

data class PalabraItem(
    @SerializedName("definition")
    val definition: String,
    @SerializedName("idWord")
    val idWord: Int,
    @SerializedName("word")
    val word: String
)