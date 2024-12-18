package edu.victorlamas.apirestwords.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import edu.victorlamas.apirestwords.R
import edu.victorlamas.apirestwords.RoomApplication
import edu.victorlamas.apirestwords.data.LocalDataSource
import edu.victorlamas.apirestwords.data.RemoteDataSource
import edu.victorlamas.apirestwords.data.WordsRepository
import edu.victorlamas.apirestwords.databinding.ActivityMainBinding
import edu.victorlamas.apirestwords.utils.WordsFilter
import edu.victorlamas.apirestwords.utils.checkConnection
import edu.victorlamas.apirestwords.utils.wordsFilter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val vm: MainViewModel by viewModels {
        val db = (application as RoomApplication).wordsDB
        val localDataSource = LocalDataSource(db.wordDao())
        val remoteDataSource = RemoteDataSource()
        val repository = WordsRepository(remoteDataSource, localDataSource)
        MainViewModelFactory(repository)
    }

    private val adapter = WordsAdapter(
        onClick = { word ->
            AlertDialog.Builder(this)
                .setTitle(word.word)
                .setMessage(word.definition)
                .setPositiveButton(getString(R.string.btn_alert_dialog), null)
                .show()
        },
        onClickFav = { word ->
            word.favourite = !word.favourite
            vm.updateWord(word)
        }
        /*onClickFav = { word ->
            if (word.favourite) {
                vm.deleteFavWord(word)
            } else {
                vm.saveFavWord(word)
            }
            word.favourite = !word.favourite
        }*/
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getWords()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                getWords()
            }
        }

        binding.mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.opt_sort -> {
                    wordsFilter = if (wordsFilter == WordsFilter.ALPHABETICAL_ASCENDANT) {
                        WordsFilter.ALPHABETICAL_DESCENDANT
                    } else {
                        WordsFilter.ALPHABETICAL_ASCENDANT
                    }
                    lifecycleScope.launch {
                        getWords()
                    }
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Obtiene las palabras y las muestra en el RecyclerView.
     * @author Víctor Lamas
     */
    private suspend fun getWords() {
        if (checkConnection(this)) {
            binding.swipeRefresh.isRefreshing = true
            combine(vm.words, vm.favWords) { apiWords, favWords ->
                apiWords.forEach { apiWord ->
                    val wordAux = favWords.find { favWord ->
                        apiWord.idWord == favWord.idWord
                    }
                    apiWord.favourite = wordAux != null
                }
                val sortedWords = when (wordsFilter) {
                    WordsFilter.ALPHABETICAL_ASCENDANT -> apiWords.sortedBy {
                            word -> word.word }
                    WordsFilter.ALPHABETICAL_DESCENDANT -> apiWords.sortedByDescending {
                            word -> word.word }
                }
                adapter.submitList(sortedWords)
                binding.swipeRefresh.isRefreshing = false
            }.catch {
                Toast.makeText(
                    this@MainActivity,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }.collect()
        } else {
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(
                this@MainActivity,
                getString(R.string.txt_noConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}