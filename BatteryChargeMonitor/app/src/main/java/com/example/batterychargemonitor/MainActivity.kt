package com.example.batterychargemonitor

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {
    var spotCount:Int = 0
    var optimalCount:Int = 0
    var badCount:Int = 0
    val displayTable = mutableListOf<SQLtable>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun ProcessData(){

        val helper = DBhelper(applicationContext)
        val db = helper.readableDatabase

        val row = db.rawQuery("SELECT * FROM batteryData", null)

        var fullStartTime:LocalDateTime
        var fullEndTime:LocalDateTime
        var previousStatus:String
        var currentStatus:String
        var recordTime:LocalDateTime
        var previousRecordTime:LocalDateTime
        var currentPercentage: Int
        var previousPercentage:Int
        var totalDischarge:Int
        var hour:LocalDateTime
        var dischargeMinutes: Int
        var previousMinute: Int
        var currentMinute:Int
        var duration:Int

        // Traverses through rows of SQLite table
        if(row.moveToFirst()) {
            recordTime = LocalDateTime.parse(row.getString(0))
            previousRecordTime = recordTime
            hour = recordTime.truncatedTo(ChronoUnit.HOURS)
            previousStatus = row.getString(1)
            dischargeMinutes = 0
            totalDischarge = 0
            previousPercentage = row.getInt(2)
            previousMinute = recordTime.minute
            fullStartTime = recordTime
            do{
                // Initialisation
                recordTime = LocalDateTime.parse(row.getString(0))
                currentMinute = recordTime.minute
                currentStatus = row.getString(1)
                currentPercentage = row.getInt(2)

                // Spot count, Optimal count & Bad count
                if(previousStatus != currentStatus)
                {
                    if(previousStatus == "Charging" && currentStatus == "Discharging")
                        spotCount += 1
                    else if(previousStatus == "Charging" && currentStatus == "Full")
                        fullStartTime = recordTime
                    else if(previousStatus == "Full" && currentStatus == "Discharging") {
                        fullEndTime = recordTime
                        if(ChronoUnit.MINUTES.between(fullStartTime, fullEndTime) > 30)
                            badCount += 1
                        else
                            optimalCount += 1
                    }
                }

                // Battery discharge percentage & duration of discharge
                // Processes for each hour

                //If current hour
                if(ChronoUnit.HOURS.between(hour, recordTime) < 1) {
                    if(currentStatus == "Discharging") {
                        duration = currentMinute - previousMinute
                        dischargeMinutes += duration
                        totalDischarge += previousPercentage - currentPercentage
                    }
                }
                // If our is changed
                else {
                    // To account for discharge duration if data was not recorded each minute
                    if(currentStatus == "Discharging")
                    {
                        dischargeMinutes += ChronoUnit.MINUTES.between(previousRecordTime, recordTime.truncatedTo(ChronoUnit.HOURS)).toInt() + 1
                    }
                    // Stores data to SQLtable class
                    val dataRow = SQLtable(
                        previousRecordTime.toLocalDate().toString(),
                        hour.hour,
                        dischargeMinutes,
                        totalDischarge
                    )
                    // Adds data to MutableList of SQLtable class
                    displayTable.add(dataRow)

                    // Resetting variables for next hour
                    hour = recordTime.truncatedTo(ChronoUnit.HOURS)
                    dischargeMinutes = 0
                    totalDischarge = 0

                    if(currentStatus == "Discharging" && previousStatus == "Discharging" && previousPercentage >= currentPercentage) {
                        dischargeMinutes += ChronoUnit.MINUTES.between(recordTime.truncatedTo(ChronoUnit.HOURS), recordTime).toInt()
                        totalDischarge += previousPercentage - currentPercentage
                    }
                        
                }

                // Storing previous row's data to variables
                previousPercentage = currentPercentage
                previousMinute = currentMinute
                previousStatus = currentStatus
                previousRecordTime = recordTime

            }while(row.moveToNext())

            // To account for last recorded hour
            val dataRow = SQLtable(
                previousRecordTime.toLocalDate().toString(),
                hour.hour,
                dischargeMinutes,
                totalDischarge
            )
            displayTable.add(dataRow)

            // To account for Bad count if battery status is currently full
            if(currentStatus == "Full" && ChronoUnit.MINUTES.between(fullStartTime, recordTime) > 30)
                badCount += 1
        }
    }


    // Function to display data on a button click
    // Data is displayed on TextView
    @RequiresApi(Build.VERSION_CODES.O)
    fun showData(view: View) {
        val tDate:TextView = findViewById(R.id.textViewDate)
        val tHour:TextView = findViewById(R.id.textViewHour)
        val tDischarge:TextView = findViewById(R.id.textViewDischarge)
        val tDuration:TextView = findViewById(R.id.textViewDuration)

        displayTable.clear()
        spotCount = 0
        optimalCount = 0
        badCount = 0

        tDate.text = ""
        tHour.text = ""
        tDischarge.text = ""
        tDuration.text = ""

        ProcessData()

        val tStatus:TextView = findViewById(R.id.textViewStatus)
        "Spot Count:       $spotCount\n\nOptimal Count: $optimalCount\n\nBad Count:        $badCount\n\n".also { tStatus.text = it }

        var sDate:String = "Date\n---------\n"
        var sHour:String= "Hour\n----------\n"
        var sDischarge:String = "Discharge%\n-------------------\n"
        var sDuration:String = "Duration\n---------------\n"

        for(row in displayTable)
        {
            sDate += row.date + "\n"
            sHour += row.hour.toString() + "\n"
            sDischarge += row.dischargePercentage.toString() + "\n"
            sDuration += row.dischargeDuration.toString() + "\n"
        }

        tDate.text = sDate
        tHour.text = sHour
        tDischarge.text = sDischarge
        tDuration.text = sDuration
    }
}
