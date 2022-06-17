package com.example.weatherapp.database

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class JsonDbHelper {
    companion object {
        fun onSuccess(json: String) {

        }

        fun toCurrentWeather(json: String): CurrentWeather {
            val jsonObj = JSONObject(json)
            val current = jsonObj.getJSONObject("current")

            println(current.toString())

            return CurrentWeather(
                null,
                dt = current.getLong("dt"),
                sunrise = if(current.has("sunrise")) current.getLong("sunrise") else 0,
                sunset = if(current.has("sunset")) current.getLong("sunset") else 0,
                temp = current.getDouble("temp"),
                feels_like = current.getDouble("feels_like"),
                pressure = current.getInt("pressure"),
                humidity = current.getInt("humidity"),
                dew_point = current.getDouble("dew_point"),
                uvi = current.getDouble("uvi"),
                clouds = current.getInt("clouds"),
                visibility = current.getInt("visibility"),
                wind_speed = current.getDouble("wind_speed"),
                wind_deg = current.getInt("wind_deg"),
                weather_id = current.getJSONArray("weather").getJSONObject(0).getInt("id"),
                short_description = current.getJSONArray("weather").getJSONObject(0)
                    .getString("main"),
                long_description = current.getJSONArray("weather").getJSONObject(0)
                    .getString("description"),
                icon = current.getJSONArray("weather").getJSONObject(0).getString("icon"),
            )
        }

        fun toListHourlyWeather(json: String): List<HourlyWeather> {
            var jObject = JSONObject(json)
            var hourly = jObject.getJSONArray("hourly")
            val list = ArrayList<HourlyWeather>()
            // {"dt":1654524000,"temp":16.79,"feels_like":16.67,"pressure":1014,"humidity":82,"dew_point":13.7,"uvi":0.36,"clouds":5,"visibility":10000,"wind_speed":0.33,"wind_deg":306,"wind_gust":1.22,"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"pop":0}
            (0 until hourly.length()).mapTo(list) {
                HourlyWeather(
                    null,
                    hourly.getJSONObject(it).getLong("dt"),
                    hourly.getJSONObject(it).getDouble("temp"),
                    hourly.getJSONObject(it).getDouble("feels_like"),
                    hourly.getJSONObject(it).getInt("pressure"),
                    hourly.getJSONObject(it).getInt("humidity"),
                    hourly.getJSONObject(it).getDouble("dew_point"),
                    hourly.getJSONObject(it).getDouble("uvi"),
                    hourly.getJSONObject(it).getInt("clouds"),
                    hourly.getJSONObject(it).getInt("visibility"),
                    hourly.getJSONObject(it).getDouble("wind_speed"),
                    hourly.getJSONObject(it).getInt("wind_deg"),
                    hourly.getJSONObject(it).getDouble("wind_gust"),
                    hourly.getJSONObject(it).getJSONArray("weather").getJSONObject(0).getInt("id"),
                    hourly.getJSONObject(it).getJSONArray("weather").getJSONObject(0)
                        .getString("main"),
                    hourly.getJSONObject(it).getJSONArray("weather").getJSONObject(0)
                        .getString("description"),
                    hourly.getJSONObject(it).getJSONArray("weather").getJSONObject(0)
                        .getString("icon"),
                    hourly.getJSONObject(it).getDouble("pop")
                )
            }

            return list
        }

        fun toListDailyWeather(json: String): List<DailyWeather> {
            var jObject = JSONObject(json)
            var hourly = jObject.getJSONArray("daily")
            val list = ArrayList<DailyWeather>()
            //{"dt":1655150400,"sunrise":1655124019,"sunset":1655177460,"moonrise":1655176320,"moonset":1655120520,"moon_phase":0.47,"temp":{"day":31.29,"min":15.72,"max":33.07,"night":22.03,"eve":28.35,"morn":19.77},"feels_like":{"day":29.17,"night":20.84,"eve":26.87,"morn":19.26},"pressure":1012,"humidity":11,"dew_point":-2.22,"wind_speed":5.68,"wind_deg":329,"wind_gust":6.86,"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"clouds":0,"pop":0.04,"uvi":10}]}
            (0 until hourly.length()).mapTo(list) {
                val current = hourly.getJSONObject(it)
                DailyWeather(
                    null,
                    current.getLong("dt"),
                    if(current.has("sunrise")) current.getLong("sunrise") else 0,
                    if(current.has("sunset")) current.getLong("sunset") else 0,
                    if(current.has("moonrise")) current.getLong("moonrise") else 0,
                    if(current.has("moonset")) current.getLong("moonset") else 0,
                    if(current.has("moon_phase")) current.getDouble("moon_phase") else 0.0,
                    current.getJSONObject("temp").getDouble("min"),
                    current.getJSONObject("temp").getDouble("max"),
                    current.getJSONObject("temp").getDouble("day"),
                    current.getJSONObject("temp").getDouble("night"),
                    current.getJSONObject("temp").getDouble("eve"),
                    current.getJSONObject("temp").getDouble("morn"),
                    current.getInt("pressure"),
                    current.getInt("humidity"),
                    current.getDouble("dew_point"),
                    current.getDouble("wind_speed"),
                    current.getInt("wind_deg"),
                    current.getDouble("wind_gust"),
                    current.getJSONArray("weather").getJSONObject(0).getInt("id"),
                    current.getJSONArray("weather").getJSONObject(0)
                        .getString("main"),
                    current.getJSONArray("weather").getJSONObject(0)
                        .getString("description"),
                    current.getJSONArray("weather").getJSONObject(0)
                        .getString("icon"),
                    current.getInt("clouds"),
                    current.getDouble("pop"),
                    if (current.has("rain")) hourly.getJSONObject(it)
                        .getDouble("rain") else 0.0,
                    if (current.has("snow")) hourly.getJSONObject(it)
                        .getDouble("snow") else 0.0,
                    current.getDouble("uvi")
                )
            }
            return list
        }

        fun toAlerts(json: String): List<Alert> {
            var jObject = JSONObject(json)
            var alerts = jObject.getJSONArray("alerts")
            val list = ArrayList<Alert>()
            (0 until alerts.length()).mapTo(list) {
                Alert(
                    null,
                    alerts.getJSONObject(it).getString("sender_name"),
                    alerts.getJSONObject(it).getString("event"),
                    alerts.getJSONObject(it).getString("description"),
                    alerts.getJSONObject(it).getLong("start"),
                    alerts.getJSONObject(it).getLong("end")
                )
            }
            return list
        }

        fun toCityList(json: String): List<String> {
            var jObject = JSONObject(json)
            var location = jObject.getJSONArray("results")
            val list = ArrayList<String>()
            (0 until location.length()).mapTo(list) {
                (location.getJSONObject(it).optString("city") + " " +
                        location.getJSONObject(it).optString("state") + ", " +
                        location.getJSONObject(it).optString("country"))
            }
            return list
        }

        fun getJsonFromAssets(fileName: String, context: Context): String? {
            /*
        open file - stream of bytes
        buffer - a byte array, read bytes from the file
         */
            var jsonString: String? = null
            val inStream = context.assets.open("MockData.json")
            val size = inStream.available()
            val buffer = ByteArray(size)
            inStream.read(buffer)
            inStream.close()
            jsonString = String(buffer, Charsets.UTF_8)
            return jsonString
        }
    }
}