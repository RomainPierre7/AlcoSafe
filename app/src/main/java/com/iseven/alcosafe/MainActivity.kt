package com.iseven.alcosafe

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton

lateinit var sharedPreferences: SharedPreferences
lateinit var alcoolText: TextView
lateinit var aJeunToggle: ToggleButton
lateinit var newDrink: Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)

        permisDef = sharedPreferences.getBoolean("permisDef", true)
        homme = sharedPreferences.getBoolean("homme", true)
        aJeun = sharedPreferences.getBoolean("aJeun", false)
        poids = sharedPreferences.getInt("poids", 75)

        alcoolText = findViewById(R.id.alcoolText)
        aJeunToggle = findViewById(R.id.aJeunToggle)
        newDrink = findViewById(R.id.drinkButton)

        newDrink.setOnClickListener {
            gramme += 10
            alcoolText.text = alcoolemieToString(alcoolemie())
        }
    }
}