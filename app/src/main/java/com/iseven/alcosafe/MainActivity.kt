package com.iseven.alcosafe

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

lateinit var sharedPreferences: SharedPreferences
lateinit var sharedEditor: SharedPreferences.Editor
val executor = Executors.newSingleThreadScheduledExecutor()
lateinit var alcoolText: TextView
lateinit var sobreText: TextView
lateinit var driveText: TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        sharedEditor = sharedPreferences?.edit()!!

        permisDef = sharedPreferences.getBoolean("permisDef", true)
        homme = sharedPreferences.getBoolean("homme", true)
        aJeun = sharedPreferences.getBoolean("aJeun", false)
        poids = sharedPreferences.getInt("poids", 75)

        alcoolText = findViewById<TextView>(R.id.alcoolText)
        sobreText = findViewById<TextView>(R.id.sobreText)
        driveText = findViewById(R.id.driveText)
        val aJeunToggle = findViewById<ToggleButton>(R.id.aJeunToggle)
        val reset = findViewById<Button>(R.id.resetButton)
        val newDrink = findViewById<ImageButton>(R.id.newDrink)

        refreshBackground()

        reset.setOnClickListener {
            gramme = 0
            refreshTexts()
            refreshBackground()
        }

        newDrink.setOnClickListener {
            gramme += 10
            refreshTexts()
            refreshBackground()
        }

        aJeunToggle.setOnClickListener {
            sharedEditor?.putBoolean("aJeun", aJeunToggle.isChecked)
            sharedEditor?.commit()
        }
    }

    override fun onStart() {
        super.onStart()
        startRunner()
    }

    override fun onResume() {
        super.onResume()
        startRunner()
    }

    override fun onPause() {
        super.onPause()
        executor.shutdown()
    }

    private fun refreshBackground() {
        val layout = findViewById<ConstraintLayout>(R.id.activity_main)
        when (permisDef) {
            true -> if (alcoolemie() <= 0.4) {
                layout.setBackgroundResource(R.drawable.green_gradient)
            } else {
                layout.setBackgroundResource(R.drawable.red_gradient)
            }
            false -> if (alcoolemie() <= 0.1) {
                layout.setBackgroundResource(R.drawable.green_gradient)
            } else {
                layout.setBackgroundResource(R.drawable.red_gradient)
            }
        }
    }

    private fun refreshTexts(){
        alcoolText.text = alcoolemieToString(alcoolemie())
        sobreText.text = sobreString()
        driveText.text = driveString()
    }

    private fun startRunner(){
        executor.scheduleAtFixedRate({
            refreshTexts()
            refreshBackground()
        }, 0, 3000, TimeUnit.MILLISECONDS)
    }
}