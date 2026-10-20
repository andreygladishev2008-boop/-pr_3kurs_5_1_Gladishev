package com.dignerdranch.anroid.prakt5

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val name: String,
    val main: MainData,
    val wind: WindData
)

data class MainData(
    val temp: Double,
    val pressure: Int
)

data class WindData(
    val speed: Double
)

class MainActivity : AppCompatActivity() {

    private val apiKey = "49da4bb0f9966e7669deac043cdb2dd7"

    private lateinit var cityInput: EditText
    private lateinit var btnGetWeather: Button
    private lateinit var tvResult: TextView
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityInput = findViewById(R.id.cityInput)
        btnGetWeather = findViewById(R.id.btnGetWeather)
        tvResult = findViewById(R.id.tvResult)

        queue = Volley.newRequestQueue(this)

        btnGetWeather.setOnClickListener {
            val city = cityInput.text.toString().trim()
            if (city.isNotEmpty()) {
                getWeather(city)
            } else {
                Toast.makeText(this, "Введите название города", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeather(city: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=ru"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val gson = Gson()
                    val weatherResponse = gson.fromJson(response, WeatherResponse::class.java)

                    val resultText = """
                        Город: ${weatherResponse.name}
                        Температура: ${weatherResponse.main.temp} °C
                        Давление: ${weatherResponse.main.pressure} гПа
                        Скорость ветра: ${weatherResponse.wind.speed} м/с """.trimIndent()

                    tvResult.text = resultText

                    // Дублируем в Logcat (как требовалось в задании)
                    android.util.Log.d("MyLog", "Успех: $resultText")

                } catch (e: Exception) {
                    tvResult.text = "Ошибка парсинга данных"
                    android.util.Log.e("MyLog", "Ошибка парсинга: ${e.message}")
                }
            },
            { error ->
                // Обработка ошибки (например, город не найден или нет интернета)
                tvResult.text = "Ошибка запроса: проверьте название города или интернет"
                android.util.Log.e("MyLog", "Volley error: ${error.message}")
            }
        )

        queue.add(stringRequest)
    }
}