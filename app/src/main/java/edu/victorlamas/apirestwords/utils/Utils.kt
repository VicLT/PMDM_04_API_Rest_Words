package edu.victorlamas.apirestwords.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Comprueba si hay conexión a internet.
 * @param context Contexto de la aplicación.
 * @return True si hay conexión, false si no la hay.
 */
fun checkConnection(context: Context): Boolean {
    val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetwork

    if (networkInfo != null) {
        val activeNetwork = cm.getNetworkCapabilities(networkInfo)
        if (activeNetwork != null)
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
    }
    return false
}

enum class WordsFilter {
    ALPHA_ASC,
    ALPHA_DESC
    /*REMOTE_ALPHABETICAL_ASCENDANT,
    REMOTE_ALPHABETICAL_DESCENDANT,
    LOCAL_ALPHABETICAL_ASCENDANT,
    LOCAL_ALPHABETICAL_DESCENDANT*/
}

/*enum class FavWordsFilter {
    ALPHA_ASC,
    ALPHA_DESC
}*/

var wordsFilter = WordsFilter.ALPHA_ASC
//var favWordsFilter = FavWordsFilter.ALPHA_ASC