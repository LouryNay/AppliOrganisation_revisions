package com.example.organisation.data

data class User(
    var name: String,
    var email: String,
    var password:String,
){

    var id:Int =-1
    var niveau=1
    var exp=0

    constructor(id:Int, name:String, email:String, password:String, niveau:Int, exp:Int):this(name,email,password){
        this.id=id
        this.niveau=niveau
        this.exp=exp


    }
}
