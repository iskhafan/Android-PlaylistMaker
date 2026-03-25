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
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
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

        val searchTextWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = android.view.View.GONE
                } else {
                    clearButton.visibility = android.view.View.VISIBLE
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) { }
        }
        searchInputField.addTextChangedListener(searchTextWatcher)
        clearButton.setOnClickListener {
            searchInputField.text.clear()
            // Hiding keyboard
            val inputMethodManager  = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInputField.windowToken, 0)
        }
    }
}