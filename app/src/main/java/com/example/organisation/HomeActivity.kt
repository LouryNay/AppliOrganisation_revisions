package com.example.organisation

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.organisation.data.Cours
import com.example.organisation.db.SPDatabase
import java.lang.Math.exp
import java.sql.Date

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {
    lateinit var listRev: ListView
    var coursArrays = ArrayList<Cours>()
    lateinit var db: SPDatabase

    fun deterProgress(univ: Int):Int{
        return (exp(univ.toDouble())*10).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db=SPDatabase(this)

        val userid=intent.getIntExtra("userid", -1)
        val cuser=db.findUserId(userid)
        val ajoutCours = findViewById<Button>(R.id.ajoutcours)
        val niv=findViewById<TextView>(R.id.niv)
        val exp=findViewById<TextView>(R.id.exp)
        val total=findViewById<TextView>(R.id.tNiv)
        val bar=findViewById<ProgressBar>(R.id.progressBar)

        var max=deterProgress(cuser.niveau)


        niv.text=cuser.niveau.toString()
        exp.text=cuser.exp.toString()
        total.text=deterProgress(cuser.niveau).toString()
        bar.progress=cuser.exp
        bar.max = max


        ajoutCours.setOnClickListener(View.OnClickListener {
            Intent(this, AjoutCoursActivity::class.java).also {
                it.putExtra("userid", userid)
                startActivity(it)
            }
        })

        listRev = findViewById(R.id.listRevision)
        coursArrays = db.findRevduJour(db.findUserId(userid))

        val adapterRev = RevAdapter(this, R.layout.item_rev, coursArrays, userid,db,max)
        listRev.adapter = adapterRev

        listRev.setOnItemClickListener { adapterView, view, position, id ->
            val clickedCours = coursArrays[position]
            Intent(this, CoursActivity::class.java).also {
                it.putExtra("cours", clickedCours.nCours)
                it.putExtra("matiere", clickedCours.matiere)
                it.putExtra("userid", userid)
                it.putExtra("max", max)
                startActivity(it)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout->{
                showLogOutDialogue()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun showLogOutDialogue(){
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Etes vous sûr de vouloir vous déconnecter?")
        builder.setPositiveButton("Oui"){dialogInterface, id ->
            val editor=this.getSharedPreferences("authentification", Context.MODE_PRIVATE).edit()
            editor.remove("is_authentificated")
            editor.apply()
            finish()
        }
        builder.setNegativeButton("Non"){dialogInterface, id ->
            dialogInterface.dismiss()
        }
        builder.setNeutralButton("Annuler"){dialogInterface, id ->
            dialogInterface.dismiss()
        }
        var alerte: AlertDialog = builder.create()
        alerte.show()
    }



}