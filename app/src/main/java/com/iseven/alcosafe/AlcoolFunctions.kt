package com.iseven.alcosafe

import android.util.Log
import java.text.DecimalFormat
import java.util.*

fun alcoolemie(){
    var sum = 0.0
    for (i in listDrinks.indices){
        sum += alcoolemieDrink(listDrinks[i])
    }
    globalAlco = sum
}

fun alcoolemieDrink(drink: Drink): Double {
    val calendar = Calendar.getInstance()
    val currentTime = calendar.timeInMillis
    val elapsedTimeMinutes = (currentTime - drink.time) / (1000 * 60)
    val alco: Double
    val gramme = 0.8 * drink.quantity * 10 * drink.percentage / 100
    if (homme){
        alco = gramme/(poids * 0.7) - (weight(drink) * (0.125/60) * jeun(elapsedTimeMinutes))
    }else{
        alco = gramme/(poids * 0.6)- (weight(drink) * (0.092/60) * jeun(elapsedTimeMinutes))
    }
    if (alco < 0.0){
        return 0.0
    } else{
        return alco
    }
}

fun alcoolemieToString(): String {
    val format = DecimalFormat("#.## g/L")
    return format.format(globalAlco)
}

fun remainingTime(target: Double): Int {
    var state = globalAlco
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
    val calendar = Calendar.getInstance()
    val currentTime = calendar.timeInMillis
    val elapsedTimeMinutes = (currentTime - lastDigestTime) / (1000 * 60)
    when (aJeun){
        true -> if (elapsedTimeMinutes < 30) {
            minutes += 30 - elapsedTimeMinutes.toInt()
        }
        false -> if (elapsedTimeMinutes < 60){
            minutes += 60 - elapsedTimeMinutes.toInt()
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
    when (permisDef) {
        true -> if (globalAlco == 0.0) {
            return "Sobre"
        }else if(globalAlco < 0.5){
            return "Sobre dans " + remainingTimeToString(remainingTime(0.0))
        } else {
            return "Conduite possible dans \n " + remainingTimeToString(remainingTime(0.5))
        }
        false -> if (globalAlco == 0.0) {
            return "Sobre"
        } else if (globalAlco < 0.2){
            return "Sobre dans " + remainingTimeToString(remainingTime(0.0))
        } else {
            return "Conduite possible dans \n" + remainingTimeToString(remainingTime(0.2))
        }
    }
}

fun driveString(): String{
    when (permisDef){
        true -> if(globalAlco == 0.0){
            return "Vous pouvez conduire"
        } else if(globalAlco < 0.5){
            return "Vous pouvez conduire (après ethylotest)"
        }else{
            return "⚠ Vous ne pouvez pas conduire ⚠"
        }
        false -> if(globalAlco == 0.0) {
            return "Vous pouvez conduire"
        } else if(globalAlco < 0.2){
            return "Vous pouvez conduire (après ethylotest)"
        }else{
            return "⚠ Vous ne pouvez pas conduire ⚠"
        }
    }
}

fun jeun(time: Long): Long{
    when (aJeun){
        true -> if (time < 30){
            return 0
        }else{
            return time - 30
        }
        false -> if (time < 60){
            return 0
        }else{
            return time - 60
        }
    }
}

fun weight(drink: Drink): Double{
    var tot = 0.0
    for (i in listDrinks.indices){
        tot += 0.8 * listDrinks[i].quantity * 10 * listDrinks[i].percentage / 100
    }
    return (0.8 * drink.quantity * 10 * drink.percentage / 100 ) / tot
}