package com.example.weatherapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.api.RetroApiInterface
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.api.WeatherViewModel
import com.example.weatherapp.database.CurrentWeather
import com.example.weatherapp.databinding.ActivityFrontPageBinding
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import timber.log.Timber

class FrontPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityFrontPageBinding
    lateinit var vm : WeatherViewModel
    lateinit var currentWeather: CurrentWeather
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrontPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var crashlytics = Firebase.crashlytics
        crashlytics.setUserId("user1")

        val api = RetroApiInterface.create()
        val repo = WeatherRepository(api, this)
        val pref = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        vm = WeatherViewModel(repo)
        //create current weather object
        currentWeather = CurrentWeather(0,0,0,0,0.0,0.0,0,0,0.0,0.0,0,0,0.0,0,0,"","","")
        //Get current weather (single not list) and bind it to recycler view adapter
        try {
            vm.getCurrentWeatherSingle()?.observe(this) {
                if (it != null) currentWeather = it
                binding.humidityTextView.text = currentWeather.humidity.toString()
                binding.windyTextView.text = currentWeather.wind_speed.toString() + " m/s"
                binding.cloudyTextView.text = currentWeather.clouds.toString() + " %"
                binding.tempTextView.text =
                    String.format("%.1f° %s", currentWeather.temp, pref.getString("tempUnits", "C"))
                binding.cloudyText.text = currentWeather.long_description
                binding.LocationText.text =
                    pref.getString("niceLocation", "") //get the saved location from preference
                Picasso.get()
                    .load("https://openweathermap.org/img/wn/" + currentWeather.icon + "@4x.png")
                    .into(binding.frontPageCloudImg)
            }
        } catch (ex: Exception) {
            Timber.log(6, ex)
        }

        try {
            vm.updateWeather(pref.getString("latitude", "0")!!, pref.getString("longitude", "0")!!)
        } catch (ex: Exception) {
            Timber.log(6, ex)
        }


        binding.forcastButton.setOnClickListener {
            try {
                var forecastIntent = Intent(this, ForecastActivity::class.java)
                forecastIntent.putExtra("niceLocation", pref.getString("niceLocation", ""))
                forecastIntent.putExtra("temp", currentWeather.temp.toString())
                forecastIntent.putExtra("weatherCondition", currentWeather.long_description)
                startActivity(forecastIntent)
            } catch (ex: Exception) {
                Timber.log(6, ex)
            }
        }

        binding.weatAlertButton.setOnClickListener {
            var alertIntent = Intent(this, WarningActivity::class.java)
            startActivity(alertIntent)
        }
    }
}