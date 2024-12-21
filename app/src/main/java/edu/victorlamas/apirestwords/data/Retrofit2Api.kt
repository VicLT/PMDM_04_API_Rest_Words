package edu.victorlamas.apirestwords.data

import edu.victorlamas.apirestwords.model.Word
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Class Retrofit2Api.kt
 * Conexión con la API Words mediante Retrofit2.
 * @author Víctor Lamas
 */
class Retrofit2Api {
    companion object {
        private const val BASE_URL = "https://www.javiercarrasco.es/api/words/"

        fun getRetrofit2Api(): Retrofit2ApiInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Retrofit2ApiInterface::class.java)
        }
    }
}

/**
 * Obtiene todas las palabras de la API.
 * @author Víctor Lamas
 *
 * @return Lista de palabras.
 */
interface Retrofit2ApiInterface {
    @GET("all")
    suspend fun getAllApiWords(): List<Word>
}