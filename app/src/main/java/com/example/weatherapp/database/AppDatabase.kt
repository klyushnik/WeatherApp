package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrentWeather::class, HourlyWeather::class, DailyWeather::class, Alert::class],
    version = 1,
    exportSchema = false)
//3
abstract class AppDatabase : RoomDatabase() {
    //4
    abstract fun weatherDao() : WeatherDao

    //5
    companion object{
        var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase? {
            if(INSTANCE == null){
                //6
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java,
                        "weather.db").build()
                }
            }

            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }

    }
}