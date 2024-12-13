package edu.victorlamas.apirestwords.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import edu.victorlamas.apirestwords.model.PalabraItem
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
    ): WordsAdapter.WordsViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: WordsAdapter.WordsViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    inner class WordsViewHolder {

    }
}

/**
 * Class WordsAdapter.kt
 * Compara dos palabras por su ID.
 * @author Víctor Lamas
 */
class WordsDiffCallback : DiffUtil.ItemCallback<PalabraItem>() {
    override fun areItemsTheSame(oldItem: PalabraItem, newItem: PalabraItem): Boolean {
        return oldItem.idWord == newItem.idWord
    }

    override fun areContentsTheSame(oldItem: PalabraItem, newItem: PalabraItem): Boolean {
        return oldItem == newItem
    }
}