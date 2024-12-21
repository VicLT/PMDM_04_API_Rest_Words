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
import edu.victorlamas.apirestwords.data.LocalDataSource
import edu.victorlamas.apirestwords.data.RemoteDataSource
import edu.victorlamas.apirestwords.data.WordsRepository
import edu.victorlamas.apirestwords.databinding.ActivityMainBinding
import edu.victorlamas.apirestwords.model.Word
import edu.victorlamas.apirestwords.utils.checkConnection
import kotlinx.coroutines.launch

/**
 * Class MainActivity.kt
 * Gestiona las operaciones y el estado de los datos en la UI.
 * @author Víctor Lamas
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentScrollPosition = 0
    private var currentFavScrollPosition = 0

    private val vm: MainViewModel by viewModels {
        val db = (application as RoomApplication).wordsDB
        val localDataSource = LocalDataSource(db.wordDao())
        val remoteDataSource = RemoteDataSource()
        val repository = WordsRepository(remoteDataSource, localDataSource)
        MainViewModelFactory(repository)
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

    /**
     * Inicializa la UI y muestra las palabras en el RecyclerView.
     * @param savedInstanceState Estado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                drawAllWords()
            }
        }
    }

    /**
     * Inicializa el menú superior y la navegación inferior.
     * Actualiza las palabras de la API si hay conexión.
     */
    override fun onStart() {
        super.onStart()

        binding.swipeRefresh.setOnRefreshListener {
            if (checkConnection(this)) {
                vm.getApiWords()
            } else {
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.txt_noConnection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.opt_sort -> {
                    vm.sortWords()
                    currentScrollPosition = 0
                    currentFavScrollPosition = 0
                    true
                }
                R.id.opt_random -> {
                    showWord(vm.getRandomWord())
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.opt_allWords -> {
                    currentFavScrollPosition = saveScrollPosition()
                    vm.isFavouriteWordsSelected = false
                    binding.swipeRefresh.isEnabled = true
                    true
                }
                R.id.opt_favWords -> {
                    currentScrollPosition = saveScrollPosition()
                    vm.isFavouriteWordsSelected = true
                    binding.swipeRefresh.isEnabled = false
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Obtiene todas las palabras de la API, las ordena y las muestra en el RV.
     * Restaura la última posición visualizada.
     * @param returnToTop Volver al principio del RecyclerView.
     */
    private suspend fun drawAllWords(returnToTop: Boolean = false) {
        adapter.submitList(emptyList())

        if (checkConnection(this)) {
            binding.swipeRefresh.isRefreshing = true

            vm.words.collect { words ->
                adapter.submitList(words) {
                    if (returnToTop) {
                        binding.recyclerView.scrollToPosition(0)
                    } else if (vm.isFavouriteWordsSelected) {
                        restoreScrollPosition(currentFavScrollPosition)
                    } else {
                        restoreScrollPosition(currentScrollPosition)
                    }
                }

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
     * Guarda la posición actual del RecyclerView.
     * @return Posición actual del RecyclerView.
     */
    private fun saveScrollPosition(): Int {
        val layoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager
        return layoutManager?.findFirstVisibleItemPosition() ?: 0
    }

    /**
     * Restaura la posición guardada en el RecyclerView.
     * @param scrollPosition Posición guardada.
     */
    private fun restoreScrollPosition(scrollPosition: Int) {
        binding.recyclerView.post {
            val layoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager
            layoutManager?.scrollToPositionWithOffset(scrollPosition, 0)
        }
    }

    /**
     * Muestra una palabra (aleatoria) en un MaterialAlertDialog.
     * @param word Palabra de la lista mostrada.
     */
    private fun showWord(word: Word?) {
        if (word != null) {
            MaterialAlertDialogBuilder(this)
                .setTitle(word.word)
                .setMessage(word.definition)
                .setPositiveButton(getString(R.string.btn_alert_dialog), null)
                .show()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.warning_title))
                .setMessage(getString(R.string.warning_message))
                .setPositiveButton(getString(R.string.btn_alert_dialog), null)
                .show()
        }
    }
}