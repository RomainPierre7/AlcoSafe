package com.iseven.alcosafe

import android.R.attr.data
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


//TODO
//Renseigner caracteristiques boissons
//historique
//supprimer boisson historique
// Notification alco (service)
// Confirmation reset
// Message premier lancement
// Nettoyer le code
// Opti

lateinit var adapter: HistoryAdapter
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

        val settings = findViewById<ImageButton>(R.id.settingsButton)
        val aJeunToggle = findViewById<Switch>(R.id.aJeunToggle)
        val reset = findViewById<ImageButton>(R.id.resetButton)

        val beer25 = findViewById<ImageButton>(R.id.beer25Button)
        val beer50 = findViewById<ImageButton>(R.id.beer50Button)
        val wine = findViewById<ImageButton>(R.id.wineButton)
        val cocktail = findViewById<ImageButton>(R.id.cocktailButton)

        Thread{
            listDrinks = drinkDAO.getAllDrinks()
            if(listDrinks.size != 0){
                lastDigestTime = drinkDAO.getDrink(drinkDAO.lastDrink()).time
            }
        }.start()
        Thread.sleep(100)
        refresh()
        refreshHistory()

        fun addDrink(name: String, percentage: Int, quantity: Int, tag: String){
            val calendar = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            var currentTimeMS: Long = calendar2.timeInMillis
            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val timePickerDialog = TimePickerDialog(this,
                        TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                            var timeMS = calendar.timeInMillis
                            val drink = Drink(0, name, percentage, quantity, timeMS, tag)
                            if (timeMS <= currentTimeMS){
                                Thread {
                                    drinkDAO.insertDrink(drink)
                                    listDrinks = drinkDAO.getAllDrinks()
                                    lastDigestTime = drinkDAO.getDrink(drinkDAO.lastDrink()).time
                                }.start()
                                Thread.sleep(100)
                                refresh()
                                refreshHistory()
                            }else{
                                Toast.makeText(this, "Cette application ne pemet pas de voyager dans le futur, la boisson n'a pas pu être ajoutée", Toast.LENGTH_LONG).show()
                            }
                        }, calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE), true
                    )
                    timePickerDialog.show()
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        reset.setOnClickListener {
            Thread{
                drinkDAO.deleteAll()
                listDrinks = drinkDAO.getAllDrinks()
            }.start()
            Thread.sleep(100)
            refresh()
            refreshHistory()
        }

        settings.setOnClickListener{
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        aJeunToggle.setOnClickListener {
            aJeun = aJeunToggle.isChecked
            sharedEditor?.putBoolean("aJeun", aJeunToggle.isChecked)
            sharedEditor?.commit()
        }

        beer25.setOnClickListener {
            val name = "Bière 25"
            val percentage = 5
            val quantity = 25
            val tag = "beer25"
            addDrink(name, percentage, quantity, tag)
        }

        beer50.setOnClickListener {
            var calendar = Calendar.getInstance()
            val name = "Bière 50"
            val percentage = 5
            val quantity = 25
            val tag = "beer50"
            addDrink(name, percentage, quantity, tag)
        }

        wine.setOnClickListener {
            var calendar = Calendar.getInstance()
            val name = "Vin"
            val percentage = 12
            val quantity = 13
            val tag = "wine"
            addDrink(name, percentage, quantity, tag)
        }

        cocktail.setOnClickListener {
            refreshHistory()
        }

        startRunner()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    private fun refreshBackground() {
        val layout = findViewById<ConstraintLayout>(R.id.activity_main)
        when (permisDef) {
            true -> if (globalAlco < 0.5) {
                layout.setBackgroundResource(R.drawable.green_gradient)
            } else {
                layout.setBackgroundResource(R.drawable.red_gradient)
            }
            false -> if (globalAlco < 0.2) {
                layout.setBackgroundResource(R.drawable.green_gradient)
            } else {
                layout.setBackgroundResource(R.drawable.red_gradient)
            }
        }
    }

    private fun refreshTexts() {
        alcoolText.text = alcoolemieToString()
        sobreText.text = sobreString()
        driveText.text = driveString()
    }

    private fun refresh(){
        alcoolemie()
        refreshTexts()
        refreshBackground()
    }

    private fun startRunner() {
        executor.scheduleAtFixedRate({
            refresh()
        }, 0, 1, TimeUnit.SECONDS)
    }

    private fun refreshHistory(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val numberOfColumns = 5
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        adapter = HistoryAdapter(this, listDrinks)
        //adapter.setOnClickListener(this)
        recyclerView.adapter = adapter
    }
}