package com.example.organisation

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.organisation.data.Cours
import com.example.organisation.data.User
import com.example.organisation.db.SPDatabase

class RevAdapter(
    var mcontext:Context,
    var ressource:Int,
    var values:ArrayList<Cours>,
    var userid: Int,
    var db: SPDatabase,
    var max : Int
):ArrayAdapter<Cours>(mcontext,ressource,values){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cours=values[position];
        val itemView = LayoutInflater.from(mcontext).inflate(ressource, parent, false)
        val tvMatiere = itemView.findViewById<TextView>(R.id.matiere)
        val tvCours=itemView.findViewById<TextView>(R.id.nCours)
        val revmenu=itemView.findViewById<ImageView>(R.id.rMenu)

        tvMatiere.text=cours.matiere
        tvCours.text=cours.nCours


        revmenu.setOnClickListener{
            val popupMenu= PopupMenu(mcontext, revmenu)
            popupMenu.menuInflater.inflate(R.menu.list_rev_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item->

                when(item.itemId){
                    R.id.term-> {
                        var user = db.findUserId(userid)
                        var exp =user.exp+((cours.niveau+1)*10)
                        if (exp>=max){
                            db.setNiveauUser(user)
                            db.setExp(exp-max,user)}
                        else{
                            db.setExp(exp,user)
                        }
                        db.setNiveauCours(cours,user)
                        db.setRevision(cours,user)

                    }
                    R.id.affi->{
                        Intent(mcontext, CoursActivity::class.java).also{
                            it.putExtra("cours", cours.nCours)
                            it.putExtra("matiere", cours.matiere)
                            mcontext.startActivity(it)
                        }
                    }
                }
                true
            }

            popupMenu.show()
        }

        return itemView
    }
}