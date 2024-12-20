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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.victorlamas.apirestwords.R
import edu.victorlamas.apirestwords.RoomApplication
import edu.victorlamas.apirestwords.data.GetWordsUseCase
import edu.victorlamas.apirestwords.data.LocalDataSource
import edu.victorlamas.apirestwords.data.RemoteDataSource
import edu.victorlamas.apirestwords.data.WordsRepository
import edu.victorlamas.apirestwords.databinding.ActivityMainBinding
import edu.victorlamas.apirestwords.model.Word
import edu.victorlamas.apirestwords.utils.WordsFilter
import edu.victorlamas.apirestwords.utils.checkConnection
import edu.victorlamas.apirestwords.utils.wordsFilter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentScrollPosition = 0
    private var currentFavScrollPosition = 0

    private val vm: MainViewModel by viewModels {
        val db = (application as RoomApplication).wordsDB
        val localDataSource = LocalDataSource(db.wordDao())
        val remoteDataSource = RemoteDataSource()
        val repository = WordsRepository(remoteDataSource, localDataSource)
        val useCase = GetWordsUseCase(repository)
        MainViewModelFactory(useCase, repository)
    }

    private val adapter = WordsAdapter(
        onClick = { word ->
            showWord(word)
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
                getAllWords("all", false)
            }
        }
        //getAllWords(false)
    }

    override fun onStart() {
        super.onStart()

        binding.swipeRefresh.setOnRefreshListener {
            getAllWords("all", false)
        }

        binding.mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.opt_sort -> {
                    wordsFilter =
                        if (wordsFilter == WordsFilter.ALPHA_ASC) {
                            WordsFilter.ALPHA_DESC
                        } else {
                            WordsFilter.ALPHA_ASC
                        }
                    if (binding.bottomNavigationView.selectedItemId == R.id.opt_allWords) {
                        getAllWords("all", true)
                    } else if (binding.bottomNavigationView.selectedItemId == R.id.opt_favWords) {
                        //getFavWords(true)
                        getAllWords("fav", true)
                    }
                    true
                }
                R.id.opt_random -> {
                    if (binding.bottomNavigationView.selectedItemId == R.id.opt_allWords) {
                        val randomWord = vm.words.value.random()
                        showWord(randomWord)
                    } else if (binding.bottomNavigationView.selectedItemId == R.id.opt_favWords) {
                        //val randomWord = vm.favWords.value.random()
                        //showWord(randomWord)
                    }
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.opt_allWords -> {
                    binding.swipeRefresh.isEnabled = true
                    currentFavScrollPosition = saveScrollPosition()
                    //Log.d("Scroll", "Fav: $currentFavScrollPosition")
                    getAllWords("all", false)
                    true
                }
                R.id.opt_favWords -> {
                    binding.swipeRefresh.isEnabled = false
                    currentScrollPosition = saveScrollPosition()
                    //Log.d("Scroll", "Normal: $currentScrollPosition")
                    //getFavWords(false)
                    getAllWords("fav", false)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Obtiene todas las palabras de la API, las ordena y las muestra en el RV.
     * @param returnToTop Si es true, vuelve a la posición 0, de lo contrario,
     * restaura la posición guardada.
     * @author Víctor Lamas
     */
    private fun getAllWords(wordsType: String, returnToTop: Boolean) {
        if (checkConnection(this)) {
            lifecycleScope.launch {
                binding.swipeRefresh.isRefreshing = true
                vm.getAllWords(wordsType)
                vm.words.collect { word ->
                    adapter.submitList(word)
                    binding.swipeRefresh.isRefreshing = false
                }
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
    /*private fun getAllWords(returnToTop: Boolean) {
        //adapter.submitList(emptyList())
        if (checkConnection(this)/* &&
            binding.bottomNavigationView.selectedItemId == R.id.opt_allWords*/) {


            lifecycleScope.launch {
                binding.swipeRefresh.isRefreshing = true
                combine(vm.words, vm.favWords) { apiWords, favWords ->
                    apiWords.forEach { apiWord ->
                        apiWord.favourite = favWords.any { favWord ->
                            favWord.idWord == apiWord.idWord }
                    }
                    /*val sortedWords = when (wordsFilter) {
                        WordsFilter.ALPHA_ASC -> apiWords.sortedBy { word ->
                            word.word?.uppercase()
                        }
                        WordsFilter.ALPHA_DESC -> apiWords.sortedByDescending { word ->
                            word.word?.uppercase()
                        }
                    }
                    return@combine sortedWords*/
                    binding.swipeRefresh.isRefreshing = false
                    apiWords
                }.catch {
                    Toast.makeText(
                        this@MainActivity,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }.collect { sortedWords ->
                    adapter.submitList(when (wordsFilter) {
                        WordsFilter.ALPHA_ASC -> sortedWords.sortedBy { word ->
                            word.word?.uppercase()
                        }
                        WordsFilter.ALPHA_DESC -> sortedWords.sortedByDescending { word ->
                            word.word?.uppercase()
                        }
                    }) {
                        if (returnToTop) {
                            scrollToTop()
                        } else {
                            restoreScrollPosition(currentScrollPosition)
                        }
                    }
                    /*if (binding.bottomNavigationView.selectedItemId == R.id.opt_allWords) {
                        adapter.submitList(sortedWords)
                    }*/
                    //binding.swipeRefresh.isRefreshing = false
                }
            }
        } else {
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(
                this@MainActivity,
                getString(R.string.txt_noConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/

    /**
     * Obtiene las palabras favoritas de Room, las ordena y las muestra en el RV.
     * @param returnToTop Si es true, vuelve a la posición 0, de lo contrario,
     * restaura la posición guardada.
     * @author Víctor Lamas
     */
    private fun getFavWords(returnToTop: Boolean) {
        //adapter.submitList(emptyList())
        //vm.getSortedFavWords()
        /*if (binding.bottomNavigationView.selectedItemId == R.id.opt_favWords) {
            lifecycleScope.launch {
                vm.favWords.collect { favWords ->
                    favWords.forEach { favWord ->
                        favWord.favourite = true
                    }
                    adapter.submitList(favWords) {
                        if (returnToTop) {
                            scrollToTop()
                        } else {
                            restoreScrollPosition(currentFavScrollPosition)
                        }
                    }
                }
            }
        }*/

        /*lifecycleScope.launch {
            vm.favWords.collect { favWords ->
                favWords.forEach { favWord ->
                    favWord.favourite = true
                }
                adapter.submitList(favWords) {
                    if (returnToTop) {
                        scrollToTop()
                    } else {
                        restoreScrollPosition(currentFavScrollPosition)
                    }
                }
            }
        }*/
    }

    /**
     * Guarda la posición actual del RecyclerView.
     * @author Víctor Lamas
     */
    private fun saveScrollPosition(): Int {
        val layoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager
        return layoutManager?.findFirstVisibleItemPosition() ?: 0
    }

    /**
     * Restaura la posición guardada en el RecyclerView.
     * @author Víctor Lamas
     */
    private fun restoreScrollPosition(scrollPosition: Int) {
        binding.recyclerView.post {
            val layoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager
            layoutManager?.scrollToPositionWithOffset(scrollPosition, 0)
        }
    }

    /**
     * Desplaza el RecyclerView a la posición 0.
     * @author Víctor Lamas
     */
    private fun scrollToTop() {
        binding.recyclerView.scrollToPosition(0)
    }

    /**
     * Muestra una palabra en un MaterialAlertDialog.
     * @param word Palabra con título y descripción.
     * @author Víctor Lamas
     */
    private fun showWord(word: Word?) {
        if (word != null) {
            MaterialAlertDialogBuilder(this)
                .setTitle(word.word)
                .setMessage(word.definition)
                .setPositiveButton(getString(R.string.btn_alert_dialog), null)
                .show()
        }
    }
}