package com.example.weatherapp.database

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class Util {
    companion object{
        fun timestampToDateFull(l : Long) : String{
            var s = ""

            val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy")
            simpleDateFormat.timeZone = TimeZone.getDefault()
            s = simpleDateFormat.format(l * 1000)

            return s
        }

        fun timestampToDayOfWeek(l : Long) : String {
            var s = ""

            val simpleDateFormat = SimpleDateFormat("EEEE")
            simpleDateFormat.timeZone = TimeZone.getDefault()
            s = simpleDateFormat.format(l * 1000)

            return s
        }

        fun timestampToDayOfWeekShort(l : Long) : String {
            var s = ""

            val simpleDateFormat = SimpleDateFormat("EEE")
            simpleDateFormat.timeZone = TimeZone.getDefault()
            s = simpleDateFormat.format(l * 1000)

            return s
        }

        fun timestampToDateShort(l : Long) : String{
            var s = ""

            val simpleDateFormat = SimpleDateFormat("MMM dd")
            simpleDateFormat.timeZone = TimeZone.getDefault()
            s = simpleDateFormat.format(l * 1000)

            return s
        }

        fun timestampToTime12(l : Long) : String {
            var s = ""

            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            simpleDateFormat.timeZone = TimeZone.getDefault()
            s = simpleDateFormat.format(l * 1000)

            return s
        }

        fun timestampToTime24(l : Long) : String {
            var s = ""

            val simpleDateFormat = SimpleDateFormat("HH:mm")
            simpleDateFormat.timeZone = TimeZone.getDefault()
            s = simpleDateFormat.format(l * 1000)

            return s
        }

        fun kelvinToCelsius(d : Double) : String {
            return round(d-273.15).toString()
        }

        fun kelvinToFahrenheit(d : Double) : String {
            return round((d-273)*(9/5)+32).toString()
        }

        //Takes a C or F and returns the appropriate unit
        //Input value should be guaranteed to be C or F
        fun getDefaultUnits(s : String) : String {
            return if (s == "F") "imperial" else "metric"
        }
    }
}