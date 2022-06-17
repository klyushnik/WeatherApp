package com.example.weatherapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class CurrentWeather(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather_id: Int,
    val short_description: String,
    val long_description: String,
    val icon: String,
) : Serializable

@Entity
data class HourlyWeather(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather_id: Int,
    val short_description: String,
    val long_description: String,
    val icon: String,
    val precipitation_probability: Double
) : Serializable

@Entity
data class DailyWeather(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moon_phase: Double,
    val temp_min: Double,
    val temp_max: Double,
    val temp_day: Double,
    val temp_night: Double,
    val temp_eve: Double,
    val temp_morn: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather_id: Int,
    val short_description: String,
    val long_description: String,
    val icon: String,
    val clouds: Int,
    val precipitation_probability: Double,
    val rain: Double?,
    val snow: Double?,
    val uvi: Double,
) : Serializable

@Entity
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val sender_name: String,
    val short_description: String,
    val long_description: String,
    val start_time: Long,
    val end_time: Long
) : Serializable