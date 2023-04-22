package com.example.organisation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.organisation.db.SPDatabase
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CoursActivity : AppCompatActivity() {

    lateinit var db: SPDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cours)

        db=SPDatabase(this)

        val nomcours=intent.getStringExtra("cours")
        val userid=intent.getIntExtra("userid", -1)
        val matiere=intent.getStringExtra("matiere")
        val cours=db.findCours(userid,matiere.toString(), nomcours.toString())
        val niveau= cours?.niveau

        val tvTitre=findViewById<TextView>(R.id.cTitre)
        val tvMatiere=findViewById<TextView>(R.id.cmatiere)
        val tvNiveau=findViewById<TextView>(R.id.niv2)
        val tvRev=findViewById<TextView>(R.id.dater)
        val bar=findViewById<ProgressBar>(R.id.progressBar2)

        val inputDateString = cours?.rDate
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val inputDate = LocalDate.parse(inputDateString, inputFormatter)

        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val outputDate = when (1) {
            1 -> inputDate.plusDays(3)
            2 -> inputDate.plusDays(7)
            3 -> inputDate.plusMonths(1)
            4 -> inputDate.plusMonths(3)
            5 -> inputDate.plusYears(1)
            6 -> inputDate.plusYears(3)
            else -> inputDate
        }

        val finalDateString = outputFormatter.format(outputDate)

        tvTitre.text=nomcours
        tvMatiere.text=matiere;
        tvNiveau.text= niveau.toString()
        tvRev.text=finalDateString
        bar.progress=niveau!!
        supportActionBar!!.title = "Révision"
    }
}

