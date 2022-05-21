package com.SeeChenAndLeYe.Pi

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class myDatabase(val context: Context, val name: String, val version: Int):SQLiteOpenHelper(context, name, null, version) {

    private val createPastProject = "CREATE TABLE $name(projectTitle TEXT, oriBitMap TEXT, newBitMap TEXT, lastEditDate TEXT)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createPastProject)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $name")
        onCreate(db)
    }
}