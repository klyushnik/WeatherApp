package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapters.DailyWeatherAdapter
import com.example.weatherapp.adapters.HourlyWeatherAdapter
import com.example.weatherapp.api.RetroApiInterface
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.api.WeatherViewModel
import com.example.weatherapp.database.DailyWeather
import com.example.weatherapp.database.HourlyWeather
import com.example.weatherapp.databinding.ActivityForecastBinding
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class ForecastActivity : AppCompatActivity() {
    lateinit var binding: ActivityForecastBinding
    lateinit var dailyWeatherList: ArrayList<DailyWeather>
    lateinit var hourlyWeatherList: ArrayList<HourlyWeather>
    lateinit var dailyWeatherAdapter: DailyWeatherAdapter
    lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    lateinit var loadingDialog: LoadingDialog
    lateinit var vm : WeatherViewModel
    var isScrolling = false
    var curentItems = 0
    var totalItems = 0
    var scrollOutItems = 0
    var offSet = 0
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences("prefs", MODE_PRIVATE)

        binding.forecastLocTextView.text = preferences.getString("niceLocation", "No location set")

        val api = RetroApiInterface.create()
        val repo = WeatherRepository(api, this)
        loadingDialog = LoadingDialog(this)

        vm = WeatherViewModel(repo)

        dailyWeatherList = ArrayList<DailyWeather>()
        var dailyRecyclerView = binding.dailyRecyclerView
        dailyWeatherAdapter = DailyWeatherAdapter(dailyWeatherList)
        dailyRecyclerView.layoutManager = LinearLayoutManager(this)
        dailyRecyclerView.adapter = dailyWeatherAdapter

        //start loading dialog
        loadingDialog.startLoadingDialog()
        //get the daily weather and bind it to the recycler view adapter

        try {
            vm.getDailyWeather()?.observe(this) {
                dailyWeatherList =
                    it as ArrayList<DailyWeather> /* = java.util.ArrayList<com.example.weatherapp.database.DailyWeather> */
                //To get min and max temp of today weather we are using the first element in list that contains today weather data
                if (!dailyWeatherList.isEmpty()) {
                    binding.forecastLowTextView.text = String.format(
                        "Low: %.1f° %s",
                        dailyWeatherList[0].temp_min,
                        preferences.getString("tempUnits", "C")
                    )
                    binding.forecastHighTextView.text = String.format(
                        "High: %.1f° %s",
                        dailyWeatherList[0].temp_max,
                        preferences.getString("tempUnits", "C")
                    )
                    dailyWeatherAdapter.setDailyWeather(
                        dailyWeatherList,
                        preferences.getString("tempUnits", "C")!!
                    )
                }
            }
        } catch (ex: Exception) {
            Timber.log(6, ex)
        }

        try {
            vm.getCurrentWeatherSingle()?.observe(this) {
                var temp = ""
                var condition = ""
                if (it == null) {
                    temp = "N/A"
                    condition = "N/A"
                } else {
                    temp =
                        String.format("%.1f° %s", it.temp, preferences.getString("tempUnits", "C"))
                    condition = it.short_description
                }
                binding.forecastWetConTextView.text = condition
                binding.forecastTemTextView.text = temp

            }
        } catch (ex: Exception) {
            Timber.log(6, ex)
        }

        //Get the first 12 hourly weather report and after that only load more if user start scrolling for more data
        hourlyWeatherList = ArrayList<HourlyWeather>()
        try {
            vm.getHourlyWeather(12, offSet)?.observe(this) {
                hourlyWeatherList =
                    it as ArrayList<HourlyWeather> /* = java.util.ArrayList<com.example.weatherapp.database.DailyWeather> */
                hourlyWeatherAdapter.setHourlyWeather(
                    hourlyWeatherList,
                    preferences.getBoolean("use24hourTime", false),
                    preferences.getString("tempUnits", "C")!!
                )
            }
        } catch (ex: Exception) {
            Timber.log(6, ex)
        }

        var hourlyRecyclerView = binding.hourlyRecyclerView
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        hourlyWeatherAdapter = HourlyWeatherAdapter(hourlyWeatherList)
        hourlyRecyclerView.layoutManager = layoutManager
        hourlyRecyclerView.adapter = hourlyWeatherAdapter

        binding.settingsButton.setOnClickListener {
            val appPreferenceIntent = Intent(this, AppPreferencesActivity::class.java)
            startForResult.launch(appPreferenceIntent)
        }
        binding.backButton.setOnClickListener {
            //finish the activity so that it's not getting stuck inside the stack
            //this avoids memory leaks
            finish()
        }
        //if user start scrolling for more data, get it from db based on offset
        hourlyRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                curentItems = layoutManager.childCount
                totalItems = layoutManager.itemCount
                scrollOutItems = layoutManager.findFirstVisibleItemPosition()
                //dismiss loading dialog
                loadingDialog.dismissDialog()

                //Size of hourForecast table is 48, we can also get size and use it
                if (isScrolling && ((curentItems + scrollOutItems) == totalItems) && offSet < 48) {
                    isScrolling = false
                    offSet += 12 //increment offset to get 12 more data once we reach end of current 12 data
                    //start loading dialog until extra 12 data get fetched
                    loadingDialog.startLoadingDialog()
                    vm.getHourlyWeather(12, offSet)?.observe(this@ForecastActivity) {
                        hourlyWeatherList.addAll(it as ArrayList<HourlyWeather>) /* = java.util.ArrayList<com.example.weatherapp.database.DailyWeather> */
                        hourlyWeatherAdapter.setHourlyWeather(hourlyWeatherList, preferences.getBoolean("use24hourTime", false), preferences.getString("tempUnits", "C")!!)
                        //dismiss loading dialog
                        loadingDialog.dismissDialog()
                    }
                }
            }
        })
    }

    //if there are any changes in preferences, update the weather and set the location
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            try {
                vm.updateWeather(
                    preferences.getString("latitude", "0")!!,
                    preferences.getString("longitude", "0")!!
                )
                binding.forecastLocTextView.setText(
                    preferences.getString(
                        "niceLocation",
                        "No location set"
                    )
                )
            } catch (ex: Exception) {
                Timber.log(6, ex)
            }
        }
    }
}