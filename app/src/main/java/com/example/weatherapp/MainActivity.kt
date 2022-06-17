package com.example.weatherapp

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.weatherapp.api.RetroApiInterface
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.api.WeatherViewModel
import com.example.weatherapp.databinding.ActivityMainBinding
import java.util.*
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.text.method.TextKeyListener.clear
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.weatherapp.api.AutocompleteAPI
import com.example.weatherapp.database.JsonDbHelper
import com.example.weatherapp.database.Util
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var repo : WeatherRepository
    lateinit var vm : WeatherViewModel
    lateinit var pref : SharedPreferences
    var cityList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        //Comment this out if you want to skip the welcome page when there's already a stored pref
        pref.edit().clear().commit()
        var apit = AutocompleteAPI.create()
        GlobalScope.launch {
            var t1 = apit.getLocation("lon")
            println(t1.body())
        }
        if(pref.getString("niceLocation","") != "") {
            val frontPageIntent = Intent(this, FrontPageActivity::class.java)
            startActivity(frontPageIntent)

            finish()
        }

        val api = RetroApiInterface.create()
        val adapter = ArrayAdapter.createFromResource(this,
            R.array.temperature_units, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        repo = WeatherRepository(RetroApiInterface.create(), this)
        vm = WeatherViewModel(repo)

        //Autocomplete text from location
        var autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,cityList)
        binding.welcomeLocationTextEdit.setAdapter(autoCompleteAdapter)

        var job : Job? = null
        binding.welcomeLocationTextEdit.addTextChangedListener {
            if (job != null)
                job!!.cancel()
            job = vm.getLocation(binding.welcomeLocationTextEdit.text.toString())
        }

        vm.autoCompleteList?.observe(this) {
            autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,it)
            binding.welcomeLocationTextEdit.setAdapter(autoCompleteAdapter)
            binding.welcomeLocationTextEdit.showDropDown()
        }

        binding.welcomeContinueButton.setOnClickListener {
            //binding.welcomeContinueButton.isVisible = false
            binding.welcomeContinueButton.visibility = View.GONE
            //binding.loadingUI.isVisible = true
            binding.loadingUI.visibility = View.VISIBLE
            GlobalScope.launch(Dispatchers.Main) { //Use main thread for toast to work
                initialize()
                //binding.welcomeContinueButton.isVisible = true
                binding.welcomeContinueButton.visibility = View.VISIBLE
                //binding.loadingProgressBar.isVisible = false
                binding.loadingUI.visibility = View.GONE
            }
        }


    }

    //Checks for valid location input and updates pref file and initializes roomDB
    //with all weather data for the given location
    suspend fun initialize() {
        val location = binding.welcomeLocationTextEdit.text.toString()
        var address : Address? = null
        if(!location.isEmpty()) {
            //Geocoder can crash if ran with empty parameters
            try {
                address = vm.searchLocation(this, location).await()
            } catch (e: IOException) {
                e.printStackTrace()
                Timber.log(6, e)
                address = null
            }
        }
        if (location != "" && address != null) {
            try {
                var address = vm.searchLocation(this, location).await()
                val lat = address!!.latitude.toString()
                val long = address!!.longitude.toString()
                //make sure pref is set before calling updateWeather
                with(pref.edit()) {
                    //we'll use niceLocation, which is a more user-friendly name - comes from address, not user input
                    //putString("location", binding.welcomeLocationTextEdit.text.toString())
                    putString("latitude", lat)
                    putString("longitude", long)
                    putString("country", address.countryName)
                    putString("tempUnits", binding.spinner.selectedItem.toString())
                    putString(
                        "units",
                        Util.getDefaultUnits(binding.spinner.selectedItem.toString())
                    )
                    putString("niceLocation", address.getAddressLine(0))
                    apply()
                }
                //The weather will be updated inside the front page activity, since this part will be skipped if
                //preferences aren't empty.
                //vm.updateWeather(lat, long).join()
                val frontPageIntent = Intent(this, FrontPageActivity::class.java)
                startActivity(frontPageIntent)
                //ternminate the activity
                finish()
            } catch (ex: Exception) {
                Timber.log(6, ex)
            }
        } else {
            Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_SHORT).show()
            delay(300) //stops user from spam clicking button
        }

    }
}