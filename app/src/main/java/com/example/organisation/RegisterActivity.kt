package com.example.organisation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.organisation.data.User
import com.example.organisation.db.SPDatabase

class RegisterActivity : AppCompatActivity() {

    lateinit var db:SPDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db=SPDatabase(this)
        val editName=findViewById<EditText>(R.id.editName)
        val editEmail=findViewById<EditText>(R.id.editEmail)
        val editPassword=findViewById<EditText>(R.id.editPassword)
        val editConfirm=findViewById<EditText>(R.id.editConfirmPassword)
        val btnSave=findViewById<Button>(R.id.btnSave)
        val error=findViewById<TextView>(R.id.errorR)

        btnSave.setOnClickListener{
            error.visibility = View.INVISIBLE
            error.text=""
            val name=editName.text.toString()
            val email=editEmail.text.toString()
            val password=editPassword.text.toString()
            val confirm=editConfirm.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()){
                error.text="Vous devez remplir tous les champs"
                error.visibility=View.VISIBLE
            }
            else{
                if(password!=confirm){
                    error.text="Les mots de passe sont différents"
                    error.visibility=View.VISIBLE
                }
                else{
                    val user= User(name,email,password)
                    val insertion=db.addUser(user)
                    if(insertion){
                        Intent(this, HomeActivity::class.java).also{
                            it.putExtra("email", email)
                            startActivity(it)
                        }
                    }
                }
            }
        }
    }
}