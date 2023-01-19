package com.iseven.alcosafe

import android.R.attr.data
import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
lateinit var db: com.iseven.alcosafe.Database
lateinit var drinkDAO: DrinkDAO
lateinit var contextMainActivity: Context
lateinit var layout: ConstraintLayout
lateinit var recyclerView: RecyclerView
val contextApplication = Application.CONTEXT_INCLUDE_CODE

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contextMainActivity = this
        layout = findViewById<ConstraintLayout>(R.id.activity_main)

        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        sharedEditor = sharedPreferences?.edit()!!
        db = Room.databaseBuilder(
            this,
            Database::class.java, "drink_database"
        ).build()
        drinkDAO = db.drinkDao()
        permisDef = sharedPreferences.getBoolean("permisDef", true)
        homme = sharedPreferences.getBoolean("homme", true)
        aJeun = sharedPreferences.getBoolean("aJeun", false)
        poids = sharedPreferences.getInt("poids", 75)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
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
        val punch = findViewById<ImageButton>(R.id.punchButton)
        val champagne = findViewById<ImageButton>(R.id.champButton)
        val whisky = findViewById<ImageButton>(R.id.whiskyButton)
        val pastis = findViewById<ImageButton>(R.id.pastisButton)
        val rum = findViewById<ImageButton>(R.id.rumButton)
        val vodka = findViewById<ImageButton>(R.id.vodkaButton)
        val liquor = findViewById<ImageButton>(R.id.liquorButton)
        val more = findViewById<ImageButton>(R.id.moreButton)

        Thread{
            listDrinks = drinkDAO.getAllDrinks()
            if(listDrinks.size != 0){
                lastDigestTime = drinkDAO.getDrink(drinkDAO.lastDrink()).time
            }
        }.start()
        Thread.sleep(100)
        refresh()
        refreshHistory()

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
            addDrink(name, percentage, quantity, tag, true)
        }

        beer25.setOnLongClickListener {
            val name = "Bière 25"
            val percentage = 5
            val quantity = 25
            val tag = "beer25"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        beer50.setOnClickListener {
            val name = "Bière 50"
            val percentage = 5
            val quantity = 50
            val tag = "beer50"
            addDrink(name, percentage, quantity, tag, true)
        }

        beer50.setOnLongClickListener {
            val name = "Bière 50"
            val percentage = 5
            val quantity = 50
            val tag = "beer50"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        wine.setOnClickListener {
            val name = "Vin"
            val percentage = 13
            val quantity = 12
            val tag = "wine"
            addDrink(name, percentage, quantity, tag, true)
        }

        wine.setOnLongClickListener {
            val name = "Vin"
            val percentage = 13
            val quantity = 12
            val tag = "wine"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        cocktail.setOnClickListener {
            val name = "Cocktail"
            val percentage = 20
            val quantity = 13
            val tag = "cocktail"
            addDrink(name, percentage, quantity, tag, true)
        }

        cocktail.setOnLongClickListener {
            val name = "Cocktail"
            val percentage = 20
            val quantity = 13
            val tag = "cocktail"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        punch.setOnClickListener {
            val name = "Punch"
            val percentage = 17
            val quantity = 17
            val tag = "punch"
            addDrink(name, percentage, quantity, tag, true)
        }

        punch.setOnLongClickListener {
            val name = "Punch"
            val percentage = 17
            val quantity = 17
            val tag = "punch"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        champagne.setOnClickListener {
            val name = "Champagne"
            val percentage = 12
            val quantity = 12
            val tag = "champagne"
            addDrink(name, percentage, quantity, tag, true)
        }

        champagne.setOnLongClickListener {
            val name = "Champagne"
            val percentage = 12
            val quantity = 12
            val tag = "champagne"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        whisky.setOnClickListener {
            val name = "Whisky"
            val percentage = 40
            val quantity = 3
            val tag = "whisky"
            addDrink(name, percentage, quantity, tag, true)
        }

        whisky.setOnLongClickListener {
            val name = "Whisky"
            val percentage = 40
            val quantity = 3
            val tag = "whisky"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        pastis.setOnClickListener {
            val name = "Pastis"
            val percentage = 45
            val quantity = 3
            val tag = "pastis"
            addDrink(name, percentage, quantity, tag, true)
        }

        pastis.setOnLongClickListener {
            val name = "Pastis"
            val percentage = 45
            val quantity = 3
            val tag = "pastis"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        rum.setOnClickListener {
            val name = "Rhum"
            val percentage = 38
            val quantity = 3
            val tag = "rum"
            addDrink(name, percentage, quantity, tag, true)
        }

        rum.setOnLongClickListener {
            val name = "Rhum"
            val percentage = 38
            val quantity = 3
            val tag = "rum"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        vodka.setOnClickListener {
            val name = "Vodka"
            val percentage = 40
            val quantity = 3
            val tag = "vodka"
            addDrink(name, percentage, quantity, tag, true)
        }

        vodka.setOnLongClickListener {
            val name = "Vodka"
            val percentage = 40
            val quantity = 3
            val tag = "vodka"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        liquor.setOnClickListener {
            val name = "Liqueur"
            val percentage = 15
            val quantity = 3
            val tag = "liquor"
            addDrink(name, percentage, quantity, tag, true)
        }

        liquor.setOnLongClickListener {
            val name = "Liqueur"
            val percentage = 15
            val quantity = 3
            val tag = "liquor"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        more.setOnClickListener {
            val name = "Liqueur"
            val percentage = 15
            val quantity = 25
            val tag = "more"
            addDrink(name, percentage, quantity, tag, true)
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

    private fun startRunner() {
        executor.scheduleAtFixedRate({
            refresh()
        }, 0, 1, TimeUnit.SECONDS)
    }
}

private fun refreshBackground() {
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

private fun refreshHistory(){
    val numberOfColumns = 5
    recyclerView.layoutManager = GridLayoutManager(contextMainActivity, numberOfColumns)
    adapter = HistoryAdapter(contextMainActivity, listDrinks)
    recyclerView.adapter = adapter
}

fun addDrink(name: String, percentage: Int, quantity: Int, tag: String, getTime: Boolean) {
    val calendar2 = Calendar.getInstance()
    var currentTimeMS: Long = calendar2.timeInMillis
    if (getTime == true) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            contextMainActivity,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    contextMainActivity,
                    TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        var timeMS = calendar.timeInMillis
                        val drink = Drink(0, name, percentage, quantity, timeMS, tag)
                        if (timeMS <= currentTimeMS) {
                            Thread {
                                drinkDAO.insertDrink(drink)
                                listDrinks = drinkDAO.getAllDrinks()
                                lastDigestTime =
                                    drinkDAO.getDrink(drinkDAO.lastDrink()).time
                            }.start()
                            Thread.sleep(100)
                            refresh()
                            refreshHistory()
                        } else {
                            Toast.makeText(
                                contextMainActivity,
                                "Cette application ne pemet pas de voyager dans le futur, la boisson n'a pas pu être ajoutée",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    calendar2.get(Calendar.HOUR_OF_DAY),
                    calendar2.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    } else {
        val drink = Drink(0, name, percentage, quantity, currentTimeMS, tag)
        Thread {
            drinkDAO.insertDrink(drink)
            listDrinks = drinkDAO.getAllDrinks()
            lastDigestTime = drinkDAO.getDrink(drinkDAO.lastDrink()).time
        }.start()
        Thread.sleep(100)
        refresh()
        refreshHistory()
    }
}

fun deleteDrink(drink: Drink){
    val builder = AlertDialog.Builder(contextMainActivity)
    builder.setTitle("Suppression de boisson")
    builder.setMessage("Voulez vous vraiment supprimer la boisson ${drink.name} ?")

    builder.setPositiveButton("Oui") { dialog, which ->
        Thread {
            drinkDAO.deleteDrink(drink)
            listDrinks = drinkDAO.getAllDrinks()
        }.start()
        Thread.sleep(100)
        refresh()
        refreshHistory()
    }

    builder.setNegativeButton("Annuler") { dialog, which ->
        Toast.makeText(
            contextMainActivity,
            "La boisson n'a pas été supprimé.", Toast.LENGTH_SHORT).show()
    }
    builder.show()
}