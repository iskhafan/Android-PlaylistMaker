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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""
    private var lastQuery: String = ""

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(TrackListApi::class.java)

    lateinit var trackAdapter: TrackAdapter

    lateinit var noFoundPlaceholder: LinearLayout
    lateinit var trackList: RecyclerView
    lateinit var connErrorPlaceholder: LinearLayout
    lateinit var refreshButton: MaterialButton
    lateinit var searchInputField: EditText

    lateinit var historyContainer: ConstraintLayout
    lateinit var historyRecyclerView: RecyclerView
    lateinit var historyAdapter: TrackAdapter
    lateinit var clearHistoryButton: MaterialButton
    lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {

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

        val clearButton = findViewById<ImageView>(R.id.clear_button)

        noFoundPlaceholder = findViewById<LinearLayout>(R.id.nothing_found_placeholder)
        trackList = findViewById<RecyclerView>(R.id.tracks_recycler_view)
        connErrorPlaceholder = findViewById<LinearLayout>(R.id.connection_error_placeholder)
        refreshButton = findViewById<MaterialButton>(R.id.refresh_button)
        searchInputField = findViewById<EditText>(R.id.search_input_field)

        historyContainer = findViewById(R.id.history_container)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        clearHistoryButton = findViewById(R.id.clear_history_button)

        searchHistory = SearchHistory(getSharedPreferences(SEARCH_HISTORY_PREFS_NAME, MODE_PRIVATE))

        // Initialize Adapter for search results
        trackAdapter = TrackAdapter(emptyList(), object : OnTrackClickListener {
            override fun onTrackClicked(track: Track) {
                // Adding new track into history list
                searchHistory.addTrack(track)
                historyAdapter.submitList(searchHistory.getHistory())
                updateHistoryVisibility()
            }
        })
        // Initialize Adapter for history of tracks
        historyAdapter = TrackAdapter(emptyList(), object : OnTrackClickListener {
            override fun onTrackClicked(track: Track) {
                // For now no need to handle presses on tracks history items
            }
        })

        // Handle cross clear text icon press evt
        clearButton.setOnClickListener {
            searchInputField.text.clear()

            val imm: InputMethodManager? = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(searchInputField.windowToken, 0)
            trackAdapter.submitList(emptyList())

            trackList.visibility = View.GONE
            connErrorPlaceholder.visibility = View.GONE
            noFoundPlaceholder.visibility = View.GONE

            // Showing history on pressing cross button
            updateHistoryVisibility()
        }


        // Overwriting only text changed evt with lambda
        searchInputField.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()

            if (text.isNullOrEmpty()) {
                clearButton.visibility = android.view.View.GONE
            } else {
                clearButton.visibility = android.view.View.VISIBLE
            }
            // Showing history if text editing results in empty string
            updateHistoryVisibility()
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

        searchInputField.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.submitList(emptyList())
            updateHistoryVisibility()
        }

        trackList.adapter = trackAdapter
        historyRecyclerView.adapter = historyAdapter
    }

    private fun performSearch() {
        // Start search only for non-empty input text
        if (searchText.isNotEmpty()) {
            // Reserving search txt for ability to call Try again
            lastQuery = searchText

            api.searchTracks(searchText).enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    // Checking response status
                    processResponse(response)
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    // Network error (offline)
                    showErrorPlaceholder()
                }
            })
        }
    }

    private fun processResponse(response:Response<SearchResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                // Handling Track Not Found evt
                if (data.results.isEmpty()) {
                    showNotFoundPlaceholder()
                    trackAdapter.submitList(emptyList())
                } else {
                    // If tracks present reading into our struct
                    val tracks = data.results.map { result ->
                        val timeInMs = result.trackTime

                        Track(
                            trackName = result.trackName,
                            artistName = result.artistName,
                            trackTime = timeInMs,
                            artworkUrl100 = result.artworkUrl100,
                            trackId = result.trackId
                        )
                    }
                    trackAdapter.submitList(tracks)

                    historyContainer.visibility = View.GONE
                    connErrorPlaceholder.visibility = View.GONE
                    noFoundPlaceholder.visibility = View.GONE
                    trackList.visibility = View.VISIBLE
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

    private fun showNotFoundPlaceholder() {
        noFoundPlaceholder.visibility = View.VISIBLE
        trackList.visibility = View.GONE
        connErrorPlaceholder.visibility = View.GONE
        historyContainer.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        connErrorPlaceholder.visibility = View.VISIBLE
        trackList.visibility = View.GONE
        noFoundPlaceholder.visibility = View.GONE
        historyContainer.visibility = View.GONE

        refreshButton.setOnClickListener { performSearch() }
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
        searchInputField.setText(value)
    }

    private fun updateHistoryVisibility() {
        // Condition: Focus is TRUE AND text is EMPTY AND history is NOT EMPTY
        val isFocused = searchInputField.hasFocus()
        val isTextEmpty = searchInputField.text.isEmpty()
        val hasHistory = searchHistory.getHistory().isNotEmpty()

        if (isFocused && isTextEmpty && hasHistory) {
            historyContainer.visibility = View.VISIBLE
            // Update the history adapter with the latest data from SharedPreferences
            historyAdapter.submitList(searchHistory.getHistory())

            trackList.visibility = View.GONE
            connErrorPlaceholder.visibility = View.GONE
            noFoundPlaceholder.visibility = View.GONE
        } else {
            historyContainer.visibility = View.GONE
        }
    }

    companion object {
        const val SEARCH_INPUT_TEXT = "SEARCH_INPUT_TEXT"
        const val LAST_QUERY_TEXT = "LAST_QUERY_TEXT"
        const val BASE_URL = "https://itunes.apple.com"

        private const val SEARCH_HISTORY_PREFS_NAME = "search_history_prefs"
    }
}