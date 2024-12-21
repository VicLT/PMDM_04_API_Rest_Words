package edu.victorlamas.apirestwords.data

import kotlinx.coroutines.flow.flow

/**
 * Class RemoteDataSource.kt
 * Maneja los datos a través de la API.
 * @author Víctor Lamas
 */
class RemoteDataSource {
    private val api = Retrofit2Api.getRetrofit2Api()

    /**
     * Obtiene todas las palabras de la API.
     * @return Flujo caliente de lista de palabras.
     */
    fun getAllApiWords() = flow {
        emit(api.getAllApiWords())
    }
}