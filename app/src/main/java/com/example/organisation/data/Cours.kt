package com.example.organisation.data

import java.util.Date

data class Cours(
    var matiere:String,
    var nCours:String,
    var cDate:String,
    var niveau:Int,
    var rDate:String,
){
    constructor(id:Int, matiere: String, nCours: String, cDate: String, niveau: Int,rDate: String):this(matiere, nCours, cDate, niveau, rDate)
}
