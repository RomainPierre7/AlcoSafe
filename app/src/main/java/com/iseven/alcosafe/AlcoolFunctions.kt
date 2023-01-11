package com.iseven.alcosafe

import java.text.DecimalFormat

var permisDef = true
var homme = true
var aJeun = false
var poids = 75
var gramme = 0

fun alcoolemie(): Double {
    if (homme){
        return gramme/(poids * 0.7)
    }else{
        return gramme/(poids * 0.6)
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

