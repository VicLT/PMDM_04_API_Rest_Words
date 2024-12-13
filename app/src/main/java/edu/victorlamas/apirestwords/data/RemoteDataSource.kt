package edu.victorlamas.apirestwords.data

import kotlinx.coroutines.flow.flow

/**
 * Class RemoteDataSource.kt
 * Maneja los datos a través de la API.
 * @author Víctor Lamas
 */
class RemoteDataSource {
    private val api = Retrofit2Api.getRetrofit2Api()

    fun fetchWords() = flow {
        emit(api.getWords())
    }
}