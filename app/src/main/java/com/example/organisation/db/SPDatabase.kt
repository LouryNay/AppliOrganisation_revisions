package com.example.organisation.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.organisation.data.Cours
import com.example.organisation.data.User
import java.time.LocalDate

class SPDatabase(
    mContext : Context
) : SQLiteOpenHelper (
    mContext,
    DB_NAME,
    null,
    DB_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableUser="""
            CREATE TABLE users(
                $USERS_ID integer PRIMARY KEY,
                $NAME varchar(50),
                $EMAIL varchar(100),
                $PASSWORD varchar(20),
                $NIVEAU integer,
                $EXPERIENCE integer
            )
        """.trimIndent()
        db?.execSQL(createTableUser)

        val createTableCours="""
            CREATE TABLE cours(
                $USERS_ID integer,
                $MATIERE varchar(40),
                $NOM_COURS varchar(50),
                $DATE_COURS date,
                $NIVEAU integer,
                $DATE_REVISION date,
                CONSTRAINT pk_t PRIMARY KEY($USERS_ID, $MATIERE, $NOM_COURS)
                
            )
        """.trimIndent()
        db?.execSQL(createTableCours)


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS exams")
        db?.execSQL("DROP TABLE IF EXISTS $USERS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $COURS_TABLE_NAME")
        onCreate(db)
    }

    fun addUser(user: User):Boolean{
        val db=this.writableDatabase
        val values= ContentValues()
        values.put(NAME, user.name)
        values.put(EMAIL, user.email)
        values.put(PASSWORD, user.password)
        val result =db.insert(USERS_TABLE_NAME, null, values).toInt()
        db.close()

        return result!=-1
    }

    fun addCours(cours: Cours, user: User):Boolean{
        val db=this.writableDatabase
        val values= ContentValues()
        values.put(USERS_ID, user.id)
        values.put(MATIERE, cours.matiere)
        values.put(NOM_COURS, cours.nCours)
        values.put(DATE_COURS, cours.cDate)
        values.put(NIVEAU, cours.niveau)
        values.put(DATE_REVISION, cours.rDate)
        val result= db.insert(COURS_TABLE_NAME, null, values).toInt()
        db.close()
        return result!=-1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setRevision(cours: Cours, user:User){
        val db=this.writableDatabase
        val dateC= LocalDate.now()
        val values = ContentValues()
        when (cours.niveau) {
            1-> values.put(DATE_REVISION, dateC.plusDays(3).toString())
            2-> values.put(DATE_REVISION, dateC.plusDays(7).toString())
            3-> values.put(DATE_REVISION, dateC.plusMonths(1).toString())
            4-> values.put(DATE_REVISION, dateC.plusMonths(3).toString())
            5-> values.put(DATE_REVISION, dateC.plusYears(1).toString())
            6-> values.put(DATE_REVISION, dateC.plusYears(3).toString())
            7-> values.put(DATE_REVISION, "null")
        }
        db.update(COURS_TABLE_NAME, values, "$USERS_ID=? AND $MATIERE=? AND $NOM_COURS=?", arrayOf(user.id.toString(),cours.matiere,cours.nCours))
        db.close()
    }


    fun findUser(email:String, password: String):User?{
        val user:User?=null
        val db=this.readableDatabase
        val cursor=db.query(USERS_TABLE_NAME, null, "$EMAIL=? AND $PASSWORD=?", arrayOf(email,password),null,null,null)
        if (cursor!=null){
            if(cursor.moveToFirst()){
                val id=cursor.getInt(0)
                val name=cursor.getString(1)
                val email=cursor.getString(2)
                val nivU=cursor.getInt(4)
                val expU=cursor.getInt(5)
                val user=User(id,name,email,"",nivU,expU)
                return user
            }

        }
        db.close()
        return user
    }

    fun findUserId(id: Int) :User {
        val db = this.readableDatabase
        val cursor = db.query(
            USERS_TABLE_NAME, null, "$USERS_ID=?",
            arrayOf(id.toString()), null, null, null
        )
        cursor.moveToFirst()
                val userid = cursor.getInt(0)
                val name = cursor.getString(1)
                val email = cursor.getString(2)
                val niveau = cursor.getInt(4)
                val exp = cursor.getInt(5)
                val user = User(userid, name, email, "", niveau, exp)

        db.close()

        return user
    }

    fun findCours(id:Int, matiere:String, ncours:String) :Cours ?{
        var cours:Cours?=null
        val db= this.readableDatabase
        val cursor=db.query(COURS_TABLE_NAME, null, "$USERS_ID=? AND $MATIERE=? AND $NOM_COURS=?", arrayOf(id.toString(),matiere,ncours),null,null,null)
        if(cursor!=null){
            if(cursor.moveToFirst()){
                val matierec=cursor.getString(1)
                val nom=cursor.getString(2)
                val date=cursor.getString(3)
                val niveau= cursor.getInt(4)
                val rev=cursor.getString(5)
                val cours = Cours(matierec,nom,date,niveau,rev)
                return cours
            }
        }
        db.close()
        return cours
    }

    fun setExp(exp:Int, user: User):Int{
        val db=this.writableDatabase

        val values = ContentValues()
        values.put(EXPERIENCE, exp)
        db.update(USERS_TABLE_NAME, values, "$USERS_ID=?", arrayOf(user.id.toString()))
        db.close()
        return exp
    }

    fun setNiveauUser(user:User):Int{
        var niveau=user.niveau+1
        val db=this.writableDatabase
        val values = ContentValues()
        values.put(NIVEAU, niveau)
        db.update(USERS_TABLE_NAME, values, "$USERS_ID=?", arrayOf(user.id.toString()))
        db.close()
        return niveau
    }

    fun setNiveauCours(cours:Cours, user:User):Int{
        var niveau=cours.niveau+1
        val db=this.writableDatabase
        val values = ContentValues()
        values.put(NIVEAU, niveau)
        db.update(COURS_TABLE_NAME, values, "$USERS_ID=? AND $MATIERE=? AND $NOM_COURS=?", arrayOf(user.id.toString(),cours.matiere,cours.nCours))
        db.close()
        return niveau
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun findRevduJour(user:User):ArrayList<Cours> {
        val listCours=ArrayList<Cours>()
        val db=readableDatabase
        val revs= "SELECT * FROM $COURS_TABLE_NAME WHERE $USERS_ID=? AND $DATE_REVISION<=?"
        val cursor=db.rawQuery(revs, arrayOf(user.id.toString(), LocalDate.now().toString() ))
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    val id=cursor.getInt(cursor.getColumnIndexOrThrow(USERS_ID))
                    val matierec=cursor.getString(cursor.getColumnIndexOrThrow(MATIERE))
                    val nom=cursor.getString(cursor.getColumnIndexOrThrow(NOM_COURS))
                    val date=cursor.getString(cursor.getColumnIndexOrThrow(DATE_COURS))
                    val niveau= cursor.getInt(cursor.getColumnIndexOrThrow(NIVEAU))
                    val rev=cursor.getString(cursor.getColumnIndexOrThrow(DATE_REVISION))
                    val cours=Cours(id,matierec,nom,date, niveau,rev)
                    listCours.add(cours)
                }while (cursor.moveToNext())
            }
        }
        db.close()
        return listCours
    }

    companion object{
        private val DB_NAME = "SP_db"
        private val DB_VERSION=3
        private val USERS_TABLE_NAME="users"
        private val COURS_TABLE_NAME="cours"
        private val USERS_ID="id"
        private val NAME="name"
        private val EMAIL="email"
        private val PASSWORD="password"
        private val MATIERE="matiere"
        private val NOM_COURS="nCours"
        private val DATE_COURS="cDate"
        private val NIVEAU="niveau"
        private val DATE_REVISION="rDate"
        private val EXPERIENCE="experience"
    }

}