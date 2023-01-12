package com.iseven.alcosafe

import java.text.DecimalFormat
import java.util.*

var permisDef = true
var homme = true
var aJeun = false
var poids = 75
var gramme = 0

fun alcoolemie(): Double {
    // Refresh all drinks'alcoolemy and sum them
    if (homme){
        return gramme/(poids * 0.7)
    }else{
        return gramme/(poids * 0.6)
    }
}

fun alcoolemieDrink(gramme: Int, timeMS: Long): Double {
    val calendar = Calendar.getInstance()
    val currentTime = calendar.timeInMillis
    val elapsedTimeMinutes = (currentTime - timeMS) / (1000 * 60)
    var alco: Double
    if (homme){
        alco = gramme/(poids * 0.7) - ((0.1/0.6) * elapsedTimeMinutes)
    }else{
        alco = gramme/(poids * 0.6)- ((0.085/0.6) * elapsedTimeMinutes)
    }
    if (alco < 0.0){
        return 0.0
    } else{
        return alco
    }
}

fun alcoolemieToString(alcoolemie: Double): String {
    val format = DecimalFormat("#.# g/L")
    return format.format(alcoolemie)
}

fun remainingTime(target: Double): Int {
    var state = alcoolemie()
    var minutes = 0
    if (homme){
        while (state > target){
            state -= 0.10/60
            minutes++
        }
    } else{
        while (state > target){
            state -= 0.085/60
            minutes++
        }
    }
    return minutes
}

fun remainingTimeToString(remainingTime: Int): String{
    val hours = remainingTime / 60
    val minutes = remainingTime % 60
    if (hours > 0){
        return "$hours heure(s) et $minutes minute(s)"
    }else{
        return "$minutes minute(s)"
    }
}

fun sobreString(): String {
    if (alcoolemie() == 0.0){
        return "Sobre"
    } else {
        return "Sobre dans " + remainingTimeToString(remainingTime(0.0))
    }
}

fun driveString(): String{
    when (permisDef){
        true -> if(alcoolemie() <= 0.4){
            return "Vous pouvez conduire"
        }else{
            return "Vous ne pouvez pas conduire"
        }
        false -> if(alcoolemie() <= 0.1){
            return "Vous pouvez conduire"
        }else{
            return "Vous ne pouvez pas conduire"
        }
    }
}

fun gramme(pourcentage: Int, quantity: Int): Int {
    return ((9 * pourcentage * quantity).toDouble() / (1000).toDouble()).toInt()
}

