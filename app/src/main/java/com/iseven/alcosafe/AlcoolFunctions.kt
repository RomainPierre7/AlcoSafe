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

fun alcoolemieToString(alcoolemie: Double): String{
    val format = DecimalFormat("#.# g/L")
    return format.format(alcoolemie)
}

