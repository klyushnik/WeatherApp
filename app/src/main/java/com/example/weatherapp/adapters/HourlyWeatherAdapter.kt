package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.database.HourlyWeather
import com.example.weatherapp.database.Util
import com.example.weatherapp.databinding.HourlyWeatherLayoutBinding
import com.squareup.picasso.Picasso

class HourlyWeatherAdapter(private var hourlyWeatherList: List<HourlyWeather>): RecyclerView.Adapter<HourlyWeatherViewHolder>() {
    var units = ""
    var use24h = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        var binding: HourlyWeatherLayoutBinding = HourlyWeatherLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return HourlyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val hourlyWeatherItemVM = hourlyWeatherList[position]
        holder.hour.text = if(use24h) Util.timestampToTime24(hourlyWeatherItemVM.dt) else Util.timestampToTime12(hourlyWeatherItemVM.dt)
        //Image icon is provided by open weather api based on icon value
        Picasso.get().load("https://openweathermap.org/img/wn/" + hourlyWeatherItemVM.icon + "@4x.png").into(holder.icon)
        holder.temp.text = String.format("%.1f° %s" ,hourlyWeatherItemVM.temp, units)
    }

    override fun getItemCount(): Int {
        return hourlyWeatherList.size
    }

    fun setHourlyWeather(hourlyWeatherList: List<HourlyWeather>, time24h: Boolean, degreeUnits: String) {
        this.hourlyWeatherList = hourlyWeatherList
        units = degreeUnits
        use24h = time24h
        notifyDataSetChanged()
    }
}

class HourlyWeatherViewHolder(binding: HourlyWeatherLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    var icon = binding.forecastHourImage
    var hour = binding.forecastHourTextView
    var temp = binding.forecastTempTextView
}