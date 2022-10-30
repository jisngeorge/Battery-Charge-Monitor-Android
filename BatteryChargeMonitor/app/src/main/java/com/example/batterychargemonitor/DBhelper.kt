package com.example.batterychargemonitor

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Class defining table and SQLite database

class DBhelper(context: Context):SQLiteOpenHelper(context,"Battery", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE batteryData(RecordTime TEXT PRIMARY KEY, BatteryStatus TEXT, BatteryPercentage INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}