package com.example.batterychargemonitor

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.util.*

class SQLiteInsert(context: Context) {
    val helper = DBhelper(context)
    val db =  helper.readableDatabase

    val bm = context.getSystemService(AppCompatActivity.BATTERY_SERVICE) as BatteryManager


    // Function to obtain battery data and store it to SQLite database
    @RequiresApi(Build.VERSION_CODES.O)
    fun insert(context: Context){
        val cv = ContentValues()

        // To obtain record time
        val time = LocalDateTime.now(TimeZone.getTimeZone("Asia/Kolkata").toZoneId())

        // To obtain battery%
        val bPercentage:Int = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        // To obtain battery status
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        var bStatus:String = ""
        when(status)
        {
            BatteryManager.BATTERY_STATUS_CHARGING -> bStatus = "Charging"
            BatteryManager.BATTERY_STATUS_FULL -> bStatus = "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> bStatus = "Discharging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> bStatus = "Discharging"
            BatteryManager.BATTERY_STATUS_UNKNOWN -> bStatus = "Unknown"
        }

        // Inserts battery data to SQLite table
        cv.put("RecordTime", time.toString())
        cv.put("BatteryStatus", bStatus)
        cv.put("BatteryPercentage", bPercentage)
        db.insert("batteryData", null, cv)
    }
}