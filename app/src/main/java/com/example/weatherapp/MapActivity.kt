package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.weatherapp.api.RetroApiInterface
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.api.WeatherViewModel
import com.example.weatherapp.database.Util
import com.example.weatherapp.databinding.ActivityMapBinding
import com.example.weatherapp.databinding.ActivitySearchBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
//import java.lang.Exception
import java.util.*
import kotlin.Exception


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var gMap: GoogleMap
    lateinit var binding: ActivityMapBinding
    lateinit var repo: WeatherRepository
    lateinit var vm: WeatherViewModel
    lateinit var pref: SharedPreferences
    lateinit var crashlytics : FirebaseCrashlytics
    var lat = 0.0
    var long = 0.0
    var location = ""
    var marker : Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        crashlytics = Firebase.crashlytics
        crashlytics.setUserId("User1")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        pref = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        repo = WeatherRepository(RetroApiInterface.create(),this)
        vm = WeatherViewModel(repo)
        try {
            vm.currentWeather.observe(this) {
                binding.locationText.text = location
                binding.temperatureText.text =
                    it.temp.toString() + "°${pref.getString("tempUnits", "C")}"

            }
        } catch (ex: Exception) {
            Timber.log(6, ex)
        }
        binding.backButtonMap.setOnClickListener {
            finish()
        }
        binding.selectLocation.setOnClickListener {
            if (location == "") {
                Toast.makeText(this, "Please select a valid location", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    setLocation()
                }
            }
        }

    }
    suspend fun setLocation() {
        try {
            vm.updateWeather(lat.toString(), long.toString()).join()
            with(pref.edit()) {
                putString("niceLocation", location)
                putString("latitude", lat.toString())
                putString("longitude", long.toString())
                apply()
            }
            //Toast.makeText(this, "Successfully updated location", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK, intent)
            intent.putExtra("niceLocation", location)
            intent.putExtra("latitude", lat.toString())
            intent.putExtra("longitude", long.toString())
            finish()
        } catch (ex: Exception) {
            Timber.log(6, ex)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setOnMapClickListener { latlng ->
            var geocoder = Geocoder(this, Locale.getDefault())
            lat = latlng.latitude
            long = latlng.longitude
            try {
                var addressList = geocoder.getFromLocation(lat, long, 1)
                if (addressList.get(0).locality != null)
                    location = addressList.get(0).locality
                else
                    location = addressList.get(0).countryName

                if (marker != null)
                    marker!!.remove()
                marker = gMap.addMarker(MarkerOptions().position(LatLng(lat, long)))

                vm.getWeather(lat.toString(), long.toString())

            } catch (e: Exception) {
                crashlytics.recordException(e)
                e.printStackTrace()
                Timber.log(6, e)
            }
        }
    }
}

