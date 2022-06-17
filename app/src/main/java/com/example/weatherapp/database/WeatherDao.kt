package com.example.weatherapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Observable

@Dao
interface WeatherDao {
    @Query("SELECT * FROM CurrentWeather")
    fun getCurrentWeather() : LiveData<List<CurrentWeather>>?

    @Query("SELECT * FROM CurrentWeather LIMIT 1")
    fun getCurrentWeatherSingle() : LiveData<CurrentWeather>?

    @Query("SELECT * FROM HourlyWeather")
    fun getHourlyWeather() : LiveData<List<HourlyWeather>>?

    @Query("SELECT * FROM HourlyWeather LIMIT :limit OFFSET :offSet")
    fun getHourlyWeather(limit: Int, offSet: Int) : LiveData<List<HourlyWeather>>?

    @Query("SELECT * FROM DailyWeather")
    fun getDailyWeather() : LiveData<List<DailyWeather>>?

    @Query("SELECT * FROM DailyWeather")
    fun getDailyWeatherObservable() : Observable<List<DailyWeather>>?

    @Query("SELECT * FROM CurrentWeather")
    fun getCurrentWeatherObservable() : Observable<List<CurrentWeather>>?

    @Query("SELECT * FROM CurrentWeather LIMIT 1")
    fun getCurrentWeatherObservableSingle() : Observable<CurrentWeather>?

    @Query("SELECT * FROM HourlyWeather")
    fun getHourlyWeatherObservable() : Observable<List<HourlyWeather>>?

    @Query("SELECT * FROM Alert")
    fun getAlerts() : LiveData<List<Alert>>

    @Query("SELECT * FROM Alert")
    fun getAlertsObservable() : Observable<List<Alert>>

    @Insert
    fun insertCurrentWeather(currentWeather: CurrentWeather)

    @Insert
    fun insertHourlyWeather(hourlyWeather: HourlyWeather)

    @Insert
    fun insertDailyWeather(dailyWeather: DailyWeather)

    @Insert
    fun insertAlert(alert: Alert)

    @Update
    fun updateCurrentWeather(currentWeather: CurrentWeather)

    @Update
    fun updateHourlyWeather(hourlyWeather: HourlyWeather)

    @Update
    fun updateDailyWeather(dailyWeather: DailyWeather)

    @Update
    fun updateAlert(alert: Alert)

    @Query("DELETE FROM CurrentWeather")
    fun clearCurrentWeather()

    @Query("DELETE FROM HourlyWeather")
    fun clearHourlyWeather()

    @Query("DELETE FROM DailyWeather")
    fun clearDailyWeather()

    @Query("DELETE FROM Alert")
    fun clearAlerts()
}