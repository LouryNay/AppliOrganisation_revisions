package com.example.organisation

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.organisation.data.Cours
import com.example.organisation.db.SPDatabase
import java.time.LocalDate
import java.time.LocalDateTime

class AjoutCoursActivity : AppCompatActivity() {

    lateinit var db: SPDatabase


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_cours)

        val userid=intent.getIntExtra("userid", -1)

        db=SPDatabase(this)

        val btn=findViewById<Button>(R.id.valcours)
        val matiere=findViewById<EditText>(R.id.edMatiere)
        val cours=findViewById<EditText>(R.id.edNCours)

        fun showConfirm() {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Cours enregistré")
            builder.setMessage("Votre cours a bien été ajouté. Rendez vous demain pour la première révision.")
            builder.setPositiveButton("OK") { dialogInterface, id ->
                Intent(this, HomeActivity::class.java).also {
                    it.putExtra("userid", userid)
                    startActivity(it)
                }
                finish()
            }

            var alerte: AlertDialog = builder.create()
            alerte.show()
        }


        btn.setOnClickListener(View.OnClickListener{
            val txtMatiere=matiere.text.toString()
            val txtCours=cours.text.toString()


            if (txtCours.trim().isEmpty()||txtMatiere.trim().isEmpty()){
                val toast = Toast.makeText(this, "Les champs matière et nom du cours sont obligatoires", Toast.LENGTH_SHORT)
                toast.show()
            }
            else{
                showConfirm()
                var dateC= LocalDate.now()
                db.findUserId(userid).let { it1 ->
                    if (it1 != null) {
                        db.addCours(Cours(txtMatiere,txtCours,dateC.toString(), 0, dateC.plusDays(1).toString()),
                            it1
                        )
                    }
                }

            }

        })


        }
    }

