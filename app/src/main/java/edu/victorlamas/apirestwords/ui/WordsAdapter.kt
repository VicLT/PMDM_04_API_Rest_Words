package edu.victorlamas.apirestwords.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.victorlamas.apirestwords.databinding.WordItemBinding
import edu.victorlamas.apirestwords.model.Word

/**
 * Class WordsAdapter.kt
 * Adapter para el RecyclerView de las palabras.
 * @author Víctor Lamas
 */
class WordsAdapter(
    val onClick: (Word) -> Unit,
    val onClickFav: (Word) -> Unit
) : ListAdapter<Word, WordsAdapter.WordsViewHolder>(WordsDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordsViewHolder {
        return WordsViewHolder(
            WordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WordsViewHolder(private val binding: WordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(word: Word) {

        }
    }
}

/**
 * Class WordsAdapter.kt
 * Compara dos palabras por su ID.
 * @author Víctor Lamas
 */
class WordsDiffCallback : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem.idPalabra == newItem.idPalabra
    }

    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem == newItem
    }
}