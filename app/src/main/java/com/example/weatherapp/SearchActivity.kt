package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.api.RetroApiInterface
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.api.WeatherViewModel
import com.example.weatherapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    lateinit var repo : WeatherRepository
    lateinit var binding: ActivitySearchBinding
    lateinit var vm : WeatherViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = WeatherRepository(RetroApiInterface.create(),this)
        vm = WeatherViewModel(repo)

    }
}