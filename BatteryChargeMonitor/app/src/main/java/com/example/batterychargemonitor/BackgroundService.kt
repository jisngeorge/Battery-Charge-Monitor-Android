package com.example.batterychargemonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

// Broadcast receiver class which receives broadcasts about boot

class BackgroundService:BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        val batteryDataRecord = SQLiteInsert(context)
        batteryDataRecord.insert(context)
    }
}