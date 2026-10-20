package com.example.smartpest.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpest.BuildConfig
import com.example.smartpest.weatherapi.NetworkResponse
import com.example.smartpest.weatherapi.RetrofitInstance
import com.example.smartpest.weatherapi.WeatherModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult
    private var request: Job? = null

    fun getData(city: String) {
        request?.cancel()
        if (city.isBlank()) {
            _weatherResult.value = NetworkResponse.Error("Enter a city or growing location.")
            return
        }
        if (BuildConfig.WEATHER_API_KEY.isBlank()) {
            _weatherResult.value = NetworkResponse.Error("Weather isn't configured. Tomato scanning and the care guide still work offline.")
            return
        }
        request = viewModelScope.launch {
            _weatherResult.value = NetworkResponse.Loading
            try {
                val response = weatherApi.getWeather(BuildConfig.WEATHER_API_KEY, city.trim())
                val data = response.body()
                _weatherResult.value = if (response.isSuccessful && data != null) NetworkResponse.Success(data)
                    else NetworkResponse.Error("Weather couldn't be loaded. Check the location and try again.")
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _weatherResult.value = NetworkResponse.Error("Weather is unavailable. Check your connection and try again.")
            }
        }
    }
}
