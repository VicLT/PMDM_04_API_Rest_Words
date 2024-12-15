package edu.victorlamas.apirestwords.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.victorlamas.apirestwords.R
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
            ).root
        )
    }

    override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

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
 * Compara dos palabras por su ID.
 * @author Víctor Lamas
 */
class WordsDiffCallback : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem.idWord == newItem.idWord
    }

    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem == newItem
    }
}