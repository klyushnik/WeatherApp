package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.database.Alert
import com.example.weatherapp.database.Util
import com.example.weatherapp.databinding.DailyWeatherLayoutBinding
import com.example.weatherapp.databinding.WarningCardLayoutBinding

class AlertAdapter(private val alertList: List<Alert>): RecyclerView.Adapter<AlertViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        var binding: WarningCardLayoutBinding = WarningCardLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alertList[position]
        holder.title.text = alert.short_description
        holder.description.text = alert.long_description
        holder.date.text = Util.timestampToDateFull(alert.start_time) +
                " - " + Util.timestampToDateFull(alert.end_time)
    }

    override fun getItemCount(): Int {
        return alertList.size
    }
}

class AlertViewHolder(binding: WarningCardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    val title : TextView = binding.t1
    val description : TextView = binding.t2
    val severity : TextView = binding.t3
    val date : TextView = binding.date
}