package com.example.smartpest.viewmodels

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpest.api.NetworkResponse
import com.example.smartpest.api.RetrofitInstance
import com.example.smartpest.api.WeatherModel
import com.example.smartpest.BuildConfig
import kotlinx.coroutines.launch
import java.io.IOException

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getData(city: String) {
        viewModelScope.launch {
            _weatherResult.value = NetworkResponse.Loading
            try {
                val response = weatherApi.getWeather(BuildConfig.WEATHER_API_KEY, city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Failed to load data: ${response.message()}")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Failed to load data: ${e.message}")
            } catch (e: HttpException) {
                _weatherResult.value = NetworkResponse.Error("HTTP Exception: ${e.message}")
            } catch (e: IOException) {
                _weatherResult.value = NetworkResponse.Error("IO Exception: ${e.message}")
            }
        }
    }
}