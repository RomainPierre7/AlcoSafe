package com.iseven.alcosafe

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.room.Room
import java.util.*
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
        val db = Room.databaseBuilder(
            this,
            Database::class.java, "drink_database"
        ).build()
        val drinkDAO = db.drinkDao()
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
        val beer = findViewById<ImageButton>(R.id.beerButton)
        val wine = findViewById<ImageButton>(R.id.wineButton)

        refreshBackground()

        reset.setOnClickListener {
            Thread{
                drinkDAO.deleteAll()
                val count = drinkDAO.count()
                runOnUiThread {
                    Log.d(TAG, "hhhhh $count")
                }
            }.start()
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

        beer.setOnClickListener {
            var calendar = Calendar.getInstance()
            val drink = Drink(0,"Bière", 5, 25, calendar.timeInMillis, 0.0)
            val gramme = gramme(drink.percentage, drink.quantity)
            drink.alcoolemieDrink = alcoolemieDrink(gramme, drink.time)
            Thread {
                drinkDAO.insertDrink(drink)
                val count = drinkDAO.count()
                runOnUiThread {
                    Log.d(TAG, "hhhhh $count")
                }
            }.start()
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

    private fun refreshTexts() {
        alcoolText.text = alcoolemieToString(alcoolemie())
        sobreText.text = sobreString()
        driveText.text = driveString()
    }

    private fun startRunner() {
        executor.scheduleAtFixedRate({
            refreshTexts()
            refreshBackground()
        }, 0, 3000, TimeUnit.MILLISECONDS)
    }
}