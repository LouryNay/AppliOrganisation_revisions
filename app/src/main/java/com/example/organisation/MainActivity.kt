package com.example.organisation



import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.organisation.data.User
import com.example.organisation.db.SPDatabase

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences


    lateinit var db:SPDatabase



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences=this.getSharedPreferences("authentification", MODE_PRIVATE)

        db=SPDatabase(this)

        val isAuthentificated= sharedPreferences.getBoolean("is_authentificated", false)
        val emailS= sharedPreferences.getString("email", null)
        val userid= sharedPreferences.getInt("userid",-1)

       if(isAuthentificated){
            Intent(this, HomeActivity::class.java).also{
                it.putExtra("email", emailS )
                it.putExtra("userid",userid)
                startActivity(it)
            }
        }



        val connect= findViewById<Button>(R.id.connect)
        val email= findViewById<EditText>(R.id.email)
        val password= findViewById<EditText>(R.id.password)
        val error=findViewById<TextView>(R.id.error)
        val register=findViewById<TextView>(R.id.creer)



        connect.setOnClickListener( View.OnClickListener{
            error.visibility=View.GONE
            val txtEmail=email.text.toString()
            val txtPassword=password.text.toString()
            if (txtEmail.trim().isEmpty() || txtPassword.trim().isEmpty()){
                error.text="Vous devez remplir tous les champs"
                error.visibility=View.VISIBLE
            }
            else{
                val user=db.findUser(txtEmail, txtPassword)
                if(user!=null){
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("is_authentificated", true)
                    editor.putString("email", txtEmail)
                    editor.putInt("userid", user.id)
                    editor.commit()
                    Intent(this, HomeActivity::class.java).also{
                        it.putExtra("userid", user.id)
                        startActivity(it)
                    }
                }
                else{
                    error.text="identifiant ou mot de passe incorrect"
                    error.visibility=View.VISIBLE
                }
            }
        })

        register.setOnClickListener{
            Intent(this, RegisterActivity::class.java).also{
                startActivity(it)
            }
        }
    }
}
