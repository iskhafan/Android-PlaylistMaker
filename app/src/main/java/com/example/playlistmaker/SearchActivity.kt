package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        val tracks = mutableListOf<Track>()
        lateinit var adapter: Adapter

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        // Handle back navigation
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val searchInputField = findViewById<EditText>(R.id.search_input_field)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        // Overwriting only text changed evt with lambda
        searchInputField.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()

            if (text.isNullOrEmpty()) {
                clearButton.visibility = android.view.View.GONE
            } else {
                clearButton.visibility = android.view.View.VISIBLE
            }
        }

        clearButton.setOnClickListener {
            searchInputField.text.clear()
            // Hiding keyboard
            val inputMethodManager  = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInputField.windowToken, 0)
        }


        tracks.add(Track("Smells Like Teen Spirit", "Nirvana", "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ))
        tracks.add(Track("Billie Jean", "Michael Jackson", "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ))
        tracks.add(Track("Stayin' Alive", "Bee Gees", "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ))
        tracks.add(Track("Whole Lotta Love","Led Zeppelin","5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ))
        tracks.add(Track("Sweet Child O'Mine", "Guns N' Roses", "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        ))

        val recyclerView = findViewById<RecyclerView>(R.id.tracks_recycler_view)

        adapter = Adapter(tracks)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val value = savedInstanceState.getString(SEARCH_INPUT_TEXT, "")
        searchText = value
        val searchInputField = findViewById<EditText>(R.id.search_input_field)
        searchInputField.setText(value)
    }

    companion object {
        const val SEARCH_INPUT_TEXT = "SEARCH_INPUT_TEXT"
    }
}