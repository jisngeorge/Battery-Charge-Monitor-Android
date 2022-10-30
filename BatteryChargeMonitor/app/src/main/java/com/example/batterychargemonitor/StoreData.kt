package com.example.batterychargemonitor

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StoreData: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED")
        {
            val intent2:Intent = Intent(context, BackgroundService::class.java)
            intent2.setAction("BatteryMonitor")
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent2, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,0, 60000, pendingIntent)
        }

    }

}