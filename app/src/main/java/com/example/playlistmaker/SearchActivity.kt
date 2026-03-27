package com.example.playlistmaker

import android.annotation.SuppressLint
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
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""

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

        val searchInputField = findViewById<EditText>(R.id.search_input_field)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        // Overwrining only text changed evt with lambda
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