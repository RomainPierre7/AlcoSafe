package com.iseven.alcosafe

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import java.util.*
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

var firstLaunch = true
var resetNumber = 0
var permisDef = true
var homme = true
var aJeun = false
var poids = 75
var globalAlco = 0.0
var lastDigestTime: Long = 0
var listDrinks: List<Drink> = emptyList()

lateinit var adapter: HistoryAdapter
lateinit var sharedPreferences: SharedPreferences
lateinit var sharedEditor: SharedPreferences.Editor
lateinit var alcoolText: TextView
lateinit var sobreText: TextView
lateinit var driveText: TextView
lateinit var db: Database
lateinit var drinkDAO: DrinkDAO
lateinit var contextMainActivity: Context
lateinit var layout: ConstraintLayout
lateinit var recyclerView: RecyclerView

class MainActivity : AppCompatActivity() {
    private var mInterstitialAd: InterstitialAd? = null
    private var TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var adRequestInter = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-5907089859538752/9615721281", adRequestInter, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError?.toString()?.let { Log.d(TAG, it) }
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        fun firstLaunchDialog(){
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(false)
            builder.setTitle("Bienvenue sur AlcoSafe !")
            builder.setMessage("Rappel :\n\n" +
                    "AlcoSafe est un estimateur d'alcool??mie bas?? sur des doses \"bar\", en fonction de votre " +
                    "poids et de votre sexe.\n\n" +
                    "AlcoSafe est uniquement un indicateur et ne repr??sente en aucun cas une preuve de votre " +
                    "alcool??mie r??elle.\n\n" +
                    "Vous ??tes l'unique responsable de votre ??tat et il est fortement recommand?? d'utiliser un " +
                    "alcootest avant de prendre le volant.")

            builder.setPositiveButton("Suivant") { dialog, which ->
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setTitle("Mode d'emploi")
                builder.setMessage("??? Cliquez sur une boisson pour rentrer une heure et l'ajouter ?? votre liste de consommation.\n\n" +
                        "??? Maintenez une boisson pour l'ajouter directement ?? votre liste de consommation avec l'heure actuelle.\n\n" +
                        "??? Maintenez une boisson de votre liste de consommation pour la supprimer.")

                builder.setPositiveButton("Suivant") { dialog, which ->
                    val builder = AlertDialog.Builder(this)
                    builder.setCancelable(false)
                    builder.setTitle("Notifications")
                    builder.setMessage("Les notifications permettent ?? AlcoSafe de vous afficher en direct " +
                        "votre taux d'alcool??mie et les informations qui vont avec.\n\n" +
                                "Si vous les d??sactivez, vous pourrez toujours les r??activer dans les param??tres de votre t??l??phone.")

                    builder.setPositiveButton("Suivant") { dialog, which ->
                        val intent = Intent(this, Settings::class.java)
                        startActivity(intent)
                    }
                    builder.show()
                }
                builder.show()
            }
            builder.show()
        }

        MobileAds.initialize(this){}
        var mainAdView = findViewById<AdView>(R.id.adViewMain)
        val adRequest = AdRequest.Builder().build()
        mainAdView.loadAd(adRequest)

        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        sharedEditor = sharedPreferences?.edit()!!

        firstLaunch = sharedPreferences.getBoolean("firstLaunch", true)
        if(firstLaunch == true){
            firstLaunchDialog()
            sharedEditor?.putBoolean("firstLaunch", false)
            sharedEditor?.commit()
        }

        contextMainActivity = this
        layout = findViewById(R.id.activity_main)

        db = Room.databaseBuilder(
            this,
            Database::class.java, "drink_database"
        ).build()
        drinkDAO = db.drinkDao()

        resetNumber = sharedPreferences.getInt("reset", 0)
        notifState = sharedPreferences.getBoolean("notifState", true)
        permisDef = sharedPreferences.getBoolean("permisDef", true)
        homme = sharedPreferences.getBoolean("homme", true)
        aJeun = sharedPreferences.getBoolean("aJeun", false)
        poids = sharedPreferences.getInt("poids", 75)

        recyclerView = findViewById(R.id.recyclerView)
        alcoolText = findViewById(R.id.alcoolText)
        sobreText = findViewById(R.id.sobreText)
        driveText = findViewById(R.id.driveText)

        val settings = findViewById<ImageButton>(R.id.settingsButton)
        val aJeunToggle = findViewById<Switch>(R.id.aJeunToggle)
        val reset = findViewById<ImageButton>(R.id.resetButton)
        val info = findViewById<ImageButton>(R.id.infoButton)

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
            if ((mInterstitialAd != null) && (resetNumber > 1)) {
                mInterstitialAd?.show(this)
            }
            resetNumber++
            sharedEditor?.putInt("reset", resetNumber)
            sharedEditor?.commit()
            stopNotif = true
            notifState = true
            sharedEditor?.putBoolean("notifState", true)
            sharedEditor?.commit()
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.cancel(1)
        }

        reset.setOnLongClickListener {
            Thread{
                drinkDAO.deleteAll()
                listDrinks = drinkDAO.getAllDrinks()
            }.start()
            Thread.sleep(100)
            refresh()
            refreshHistory()
            stopNotif = true
            notifState = true
            sharedEditor?.putBoolean("notifState", true)
            sharedEditor?.commit()
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.cancel(1)
            true
        }

        info.setOnClickListener {
            infoDialog()
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
            val name = "Bi??re 25"
            val percentage = 5
            val quantity = 25
            val tag = "beer25"
            addDrink(name, percentage, quantity, tag, true)
        }

        beer25.setOnLongClickListener {
            val name = "Bi??re 25"
            val percentage = 5
            val quantity = 25
            val tag = "beer25"
            addDrink(name, percentage, quantity, tag, false)
            true
        }

        beer50.setOnClickListener {
            val name = "Bi??re 50"
            val percentage = 5
            val quantity = 50
            val tag = "beer50"
            addDrink(name, percentage, quantity, tag, true)
        }

        beer50.setOnLongClickListener {
            val name = "Bi??re 50"
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
            val percentage = 12
            val quantity = 10
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
            var percentage = 0
            var quantity = 0
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Entrez le degr?? d'alcool :")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)
            builder.setPositiveButton("OK") { _, _ ->
                val numPercentage = input.text.toString().toIntOrNull()
                if ((numPercentage != null) && (numPercentage > 0)){
                    percentage = numPercentage
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Entrez la quantit?? de boisson en cl :")
                    val input = EditText(this)
                    input.inputType = InputType.TYPE_CLASS_NUMBER
                    builder.setView(input)
                    builder.setPositiveButton("OK") { _, _ ->
                        val numQuantity = input.text.toString().toIntOrNull()
                        if ((numQuantity != null) && (numQuantity > 0)){
                            quantity = numQuantity
                            val name = "Autre"
                            val tag = "more"
                            addDrink(name, percentage, quantity, tag, true)
                        }else{
                            Toast.makeText(this, "La saisie n'est pas valable, rentrez seuleument des entiers naturels strictement positifs", Toast.LENGTH_LONG)
                        }
                    }
                    builder.setNegativeButton("Annuler") { _, _ -> }
                    builder.show()
                }else{
                    Toast.makeText(this, "La saisie n'est pas valable, rentrez seuleument des entiers naturels strictement positifs", Toast.LENGTH_LONG)
                }
            }
            builder.setNegativeButton("Annuler") { _, _ -> }
            builder.show()
        }

        val intent = Intent(this, AlcoService::class.java)
        startService(intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, AlcoService::class.java)
        stopService(intent)
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

fun refresh(){
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
                        if (timeMS <= currentTimeMS + 60 * 1000) {
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
                                "Cette application ne pemet pas de voyager dans le futur, la boisson n'a pas pu ??tre ajout??e",
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
    builder.setMessage("Voulez vous vraiment supprimer la boisson \"${drink.name}\" ?")

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
            "La boisson n'a pas ??t?? supprim??.", Toast.LENGTH_SHORT).show()
    }
    builder.show()
}

fun infoDialog() {
    val builder = AlertDialog.Builder(contextMainActivity)
    builder.setTitle("Bienvenue sur AlcoSafe !")
    builder.setMessage("Rappel :\n\n" +
            "AlcoSafe est un estimateur d'alcool??mie bas?? sur des doses \"bar\", en fonction de votre " +
            "poids et de votre sexe.\n\n" +
            "AlcoSafe est uniquement un indicateur et ne repr??sente en aucun cas une preuve de votre " +
            "alcool??mie r??elle.\n\n" +
            "Vous ??tes l'unique responsable de votre ??tat et il est fortement recommand?? d'utiliser un " +
            "alcootest avant de prendre le volant.")

    builder.setPositiveButton("Suivant") { dialog, which ->
        val builder = AlertDialog.Builder(contextMainActivity)
        builder.setTitle("Mode d'emploi")
        builder.setMessage(
            "??? Cliquez sur une boisson pour rentrer une heure et l'ajouter ?? votre liste de consommation.\n\n" +
                    "??? Maintenez une boisson pour l'ajouter directement ?? votre liste de consommation avec l'heure actuelle.\n\n" +
                    "??? Maintenez une boisson de votre liste de consommation pour la supprimer."
        )

        builder.setPositiveButton("OK") { dialog, which ->
        }
        builder.show()
    }
    builder.show()
}

fun requestPermissionNotification(context: Context, activity: Activity){
    if (ContextCompat.checkSelfPermission(context,
            Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.POST_NOTIFICATIONS)) {
        } else {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0)
        }
    }
}