package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""
    private var lastQuery: String = ""

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(TrackListApi::class.java)

    lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.tracks_recycler_view)

        val toolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        // Handle back navigation
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val searchInputField = findViewById<EditText>(R.id.search_input_field)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        // Initialize Adapter
        adapter = Adapter(emptyList())
        recyclerView.adapter = adapter

        // Handle cross clear text icon press evt
        clearButton.setOnClickListener {
            searchInputField.text.clear()

            val imm: InputMethodManager? = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(searchInputField.windowToken, 0)
            adapter.submitList(emptyList())
            hideErrorPlaceholder()
        }


        // Overwriting only text changed evt with lambda
        searchInputField.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()

            if (text.isNullOrEmpty()) {
                clearButton.visibility = android.view.View.GONE
            } else {
                clearButton.visibility = android.view.View.VISIBLE
            }
        }

        // Handle virtual keyboard Enter button press
        searchInputField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                //Calling track search
                performSearch()
                true
            } else {
                false
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    }

    private fun performSearch() {
        // Start search only for non-empty input text
        if (searchText.isNotEmpty()) {
            // Reserving search txt for ability to call Try again
            lastQuery = searchText

            api.searchTracks(searchText).enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    // Checking response status
                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            // Handling Track Not Found evt
                            if (data.results.isEmpty()) {
                                showNotFoundPlaceholder()
                                adapter.submitList(emptyList())
                            } else {
                                // If tracks present reading into our struct
                                val tracks = data.results.map { result ->
                                    val timeInMs = result.trackTime
                                    val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                                        Date(timeInMs)
                                    )

                                    Track(
                                        trackName = result.trackName,
                                        artistName = result.artistName,
                                        trackTime = timeInMs,
                                        artworkUrl100 = result.artworkUrl100
                                    )
                                }
                                adapter.submitList(tracks)
                                hideErrorPlaceholder()
                            }
                        } ?: run {
                            // Handling empty response body (but status OK)
                            showErrorPlaceholder()
                        }
                    } else {
                        // Handling server/communication error
                        showErrorPlaceholder()
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    // Network error (offline)
                    showErrorPlaceholder()
                }
            })
        }
    }

    private fun showNotFoundPlaceholder() {

        findViewById<LinearLayout>(R.id.nothing_found_placeholder).visibility = View.VISIBLE
        findViewById<RecyclerView>(R.id.tracks_recycler_view).visibility = View.GONE
        findViewById<LinearLayout>(R.id.connection_error_placeholder).visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        findViewById<LinearLayout>(R.id.connection_error_placeholder).visibility = View.VISIBLE
        findViewById<RecyclerView>(R.id.tracks_recycler_view).visibility = View.GONE
        findViewById<LinearLayout>(R.id.nothing_found_placeholder).visibility = View.GONE

        findViewById<com.google.android.material.button.MaterialButton>(R.id.refresh_button)
            .setOnClickListener { performSearch() }
    }

    private fun hideErrorPlaceholder() {
        findViewById<LinearLayout>(R.id.nothing_found_placeholder).visibility = View.GONE
        findViewById<LinearLayout>(R.id.connection_error_placeholder).visibility = View.GONE
        findViewById<RecyclerView>(R.id.tracks_recycler_view).visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT_TEXT, searchText)
        outState.putString(LAST_QUERY_TEXT, lastQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val value = savedInstanceState.getString(SEARCH_INPUT_TEXT, "")
        searchText = value
        val lastQueryVal = savedInstanceState.getString(LAST_QUERY_TEXT, "")
        lastQuery = lastQueryVal
        val searchInputField = findViewById<EditText>(R.id.search_input_field)
        searchInputField.setText(value)
    }

    companion object {
        const val SEARCH_INPUT_TEXT = "SEARCH_INPUT_TEXT"
        const val LAST_QUERY_TEXT = "LAST_QUERY_TEXT"
    }
}