package com.example.batterychargemonitor

// Class to store processed data
class SQLtable(date: String, hour: Int, dischargeDuration: Int, dischargePercentage: Int) {
    val date = date
    val hour = hour
    val dischargeDuration = dischargeDuration
    val dischargePercentage = dischargePercentage
}