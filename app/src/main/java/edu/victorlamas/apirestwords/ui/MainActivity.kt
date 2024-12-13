package edu.victorlamas.apirestwords.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.victorlamas.apirestwords.RoomApplication
import edu.victorlamas.apirestwords.data.LocalDataSource
import edu.victorlamas.apirestwords.data.RemoteDataSource
import edu.victorlamas.apirestwords.data.WordsRepository
import edu.victorlamas.apirestwords.databinding.ActivityMainBinding

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

        },
        onClickFav = { word ->

        }
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
    }
}