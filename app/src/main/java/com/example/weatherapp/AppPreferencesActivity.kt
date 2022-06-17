package com.example.weatherapp

import android.R
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.example.weatherapp.api.RetroApiInterface
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.api.WeatherViewModel
import com.example.weatherapp.databinding.ActivityAppPreferencesBinding
import kotlinx.coroutines.Job

class AppPreferencesActivity : AppCompatActivity() {
    val okColor = Color.parseColor("#E0E0E0")
    val errColor = Color.parseColor("#FF9D8C")
    val NO_LOC_TEXT = "No location set"

    lateinit var binding: ActivityAppPreferencesBinding
    lateinit var preferences : SharedPreferences
    lateinit var geocoder: Geocoder
    lateinit var  locationList: ArrayList<Address>
    lateinit var currLocationText : String
    lateinit var repo : WeatherRepository
    lateinit var vm : WeatherViewModel
    var use24h = false
    var useMetricUnits = false
    var changedSettings = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences("prefs", MODE_PRIVATE)
        geocoder = Geocoder(this)
        locationList = ArrayList()
        repo = WeatherRepository(RetroApiInterface.create(), this)
        vm = WeatherViewModel(repo)

        //load settings and apply to controls in the xml
        currLocationText = preferences.getString("niceLocation", NO_LOC_TEXT)!!
        use24h = preferences.getBoolean("use24hourTime", false)
        useMetricUnits = preferences.getString("tempUnits", "C") == "C"

        binding.switch24h.isChecked = use24h
        binding.switchCelcius.isChecked = useMetricUnits
        binding.currentLocationLabel.setText(currLocationText)
        binding.locationTextinputlayout.boxBackgroundColor = okColor

        println(preferences.getString("niceLocation", "no location"))

        //Autocomplete text from location
        var autoCompleteAdapter = ArrayAdapter(this, R.layout.simple_list_item_1,listOf(""))
        binding.zipEditText.setAdapter(autoCompleteAdapter)

        var job : Job? = null
        binding.zipEditText.addTextChangedListener {
            if (job != null)
                job!!.cancel()
            job = vm.getLocation(binding.zipEditText.text.toString())
        }

        vm.autoCompleteList?.observe(this) {
            autoCompleteAdapter = ArrayAdapter(this, R.layout.simple_list_item_1,it)
            binding.zipEditText.setAdapter(autoCompleteAdapter)
            binding.zipEditText.showDropDown()
        }

        binding.applyButton.setOnClickListener {
            val searchTerm = binding.zipEditText.text.toString()
            var loc = ""
            if(searchTerm.isEmpty()){
                binding.locationTextinputlayout.boxBackgroundColor = errColor
                loc = NO_LOC_TEXT
                Toast.makeText(this, "Please enter a Postal code or a city name and state!", Toast.LENGTH_LONG).show()
            } else {
                //geocoder returns a list, so we'll use a list as well
                val locList = geocoder.getFromLocationName(searchTerm, 5)
                locationList.clear()
                locationList.addAll(locList)
                if(locationList.isEmpty()) {
                    binding.locationTextinputlayout.boxBackgroundColor = errColor
                    Toast.makeText(this, "Could not resolve the location!", Toast.LENGTH_LONG).show()
                    loc = NO_LOC_TEXT
                } else {
                    binding.locationTextinputlayout.boxBackgroundColor = okColor
                    loc = locationList.get(0).getAddressLine(0)
                    with(preferences.edit()){
                        putString("niceLocation", loc)
                        putString("latitude", locationList.get(0).latitude.toString())
                        putString("longitude", locationList.get(0).longitude.toString())
                        apply()
                    }
                }
            }
            changedSettings = true
            binding.currentLocationLabel.setText(loc)
        }

        binding.switch24h.setOnCheckedChangeListener { button, isChecked ->
            with(preferences.edit()){
                putBoolean("use24hourTime", isChecked)
                changedSettings = true
                apply()
            }
        }

        binding.switchCelcius.setOnCheckedChangeListener { button, isChecked ->
            with(preferences.edit()){
                putString("tempUnits", if (isChecked) "C" else "F")
                putString("units", if (isChecked) "metric" else "imperial")
                changedSettings = true
                apply()
            }
        }

        binding.mapSearch.setOnClickListener {

            val mapIntent = Intent(this, MapActivity::class.java)
            //startActivity(mapIntent)
            startForResult.launch(mapIntent)
        }

        binding.backButtonSettings.setOnClickListener {
            //set the result so that the calling activity could call vm.updateWeather() if necessary
            if(changedSettings){
                setResult(RESULT_OK)
            } else {
                setResult(RESULT_CANCELED)
            }
            finish()
        }

    }

    //this handles the result from the map activity
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            currLocationText = intent!!.getStringExtra("niceLocation")!!
            val long = intent.getStringExtra("longitude")
            val lat = intent.getStringExtra("latitude")
            binding.currentLocationLabel.setText(currLocationText)
            binding.zipEditText.setText(currLocationText)
            changedSettings = true
            Toast.makeText(this, "Successfully updated location", Toast.LENGTH_SHORT).show()
        }
    }
}