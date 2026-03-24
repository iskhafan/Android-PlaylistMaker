package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// Disable edge to edge for study simplicity
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

//  Using anonymus class as click handler
        val search_btn = findViewById<MaterialButton>(R.id.search_btn)
        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) { Toast.makeText(this@MainActivity, "Нажали на Поиск!", Toast.LENGTH_SHORT).show()
            }
        }
        search_btn.setOnClickListener(imageClickListener)

//  Using lambda as click handler
        val library_btn = findViewById<MaterialButton>(R.id.library_btn)
        library_btn.setOnClickListener   {
            Toast.makeText(this@MainActivity, "Нажали на Медиатека!", Toast.LENGTH_SHORT).show()
        }
        val settings_btn = findViewById<MaterialButton>(R.id.settings_btn)
        settings_btn.setOnClickListener   {
            Toast.makeText(this@MainActivity, "Нажали на Настройки!", Toast.LENGTH_SHORT).show()
        }

// Disable ViewCompat for study simplicity
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
}