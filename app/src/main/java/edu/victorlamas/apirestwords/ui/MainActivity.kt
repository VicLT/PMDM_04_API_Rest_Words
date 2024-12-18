package edu.victorlamas.apirestwords.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
            MaterialAlertDialogBuilder(this)
                .setTitle(word.word)
                .setMessage(word.definition)
                .setPositiveButton(getString(R.string.btn_alert_dialog), null)
                .show()
        },
        onClickFav = { word ->
            word.favourite = !word.favourite
            vm.updateWord(word)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
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

        /*val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (positionStart == 0 && positionStart == layoutManager.findFirstCompletelyVisibleItemPosition()) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        })*/

        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getAllWords()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                getAllWords()
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
                        getAllWords()
                    }
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.optBottom_all -> {
                    lifecycleScope.launch {
                        getAllWords()
                    }
                    true
                }
                R.id.opt_favorites -> {
                    lifecycleScope.launch {
                        vm.favWords.collect { favWords ->
                            adapter.submitList(favWords)
                        }
                    }
                    //adapter.submitList(vm.favWords)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Obtiene las palabras, las ordena y las muestra en el RecyclerView.
     * @author Víctor Lamas
     */
    private suspend fun getAllWords() {
        adapter.submitList(emptyList())
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
                            word -> word.word?.uppercase()
                    }
                    WordsFilter.ALPHABETICAL_DESCENDANT -> apiWords.sortedByDescending {
                        word -> word.word?.uppercase()
                    }
                }
                return@combine sortedWords
            }.catch {
                Toast.makeText(
                    this@MainActivity,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }.collect { sortedWords ->
                adapter.submitList(sortedWords)
                binding.swipeRefresh.isRefreshing = false
            }
        } else {
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(
                this@MainActivity,
                getString(R.string.txt_noConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Obtiene las palabras favoritas, las ordena y las muestra en el RecyclerView.
     * @author Víctor Lamas
     */
    private suspend fun getFavWords() {

    }
}