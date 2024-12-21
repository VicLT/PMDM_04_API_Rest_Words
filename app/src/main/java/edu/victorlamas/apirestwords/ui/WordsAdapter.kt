package edu.victorlamas.apirestwords.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.victorlamas.apirestwords.R
import edu.victorlamas.apirestwords.databinding.WordItemBinding
import edu.victorlamas.apirestwords.model.Word

/**
 * Class WordsAdapter.kt
 * Adapter para el RecyclerView de las palabras.
 * @author Víctor Lamas
 *
 * @param onClick Función que se ejecuta al hacer click en una palabra.
 * @param onClickFav Función que se ejecuta al hacer click en el icono de favorito.
 */
class WordsAdapter(
    val onClick: (Word) -> Unit,
    val onClickFav: (Word) -> Unit
) : ListAdapter<Word, WordsAdapter.WordsViewHolder>(WordsDiffCallback()) {

    /**
     * Crea un ViewHolder para Word.
     * @param parent Grupo de vistas al que se añade el ViewHolder.
     * @param viewType Tipo de vista.
     * @return ViewHolder de Word.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordsViewHolder {
        return WordsViewHolder(
            WordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    /**
     * Vincula los datos de la palabra con el ViewHolder.
     * @param holder ViewHolder de Word.
     * @param position Posición de la palabra en la lista.
     */
    override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Clase interna WordsViewHolder.
     * ViewHolder para las palabras.
     * @param view Vista de la palabra.
     */
    inner class WordsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = WordItemBinding.bind(view)
        fun bind(word: Word) {
            bind.tvWord.text = word.word
            bind.root.setOnClickListener {
                onClick(word)
            }
            bind.ivFav.setOnClickListener {
                onClickFav(word)
                notifyItemChanged(adapterPosition)
            }
            bind.ivFav.setImageState(
                intArrayOf(R.attr.state_on),
                word.favourite
            )
        }
    }
}

/**
 * Class WordsAdapter.kt
 * Compara dos palabras.
 * @author Víctor Lamas
 */
class WordsDiffCallback : DiffUtil.ItemCallback<Word>() {
    /**
     * Comprueba si dos palabras son iguales por su ID.
     * @param oldItem Palabra antigua.
     * @param newItem Palabra nueva.
     * @return True si las palabras son iguales.
     */
    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem.idWord == newItem.idWord
    }

    /**
     * Comprueba si el contenido de dos palabras es el mismo.
     * @param oldItem Palabra antigua.
     * @param newItem Palabra nueva.
     * @return True si el contenido de las palabras es el mismo.
     */
    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem == newItem
    }
}