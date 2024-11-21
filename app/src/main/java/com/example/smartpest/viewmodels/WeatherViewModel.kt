package com.example.smartpest.viewmodels

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpest.api.NetworkResponse
import com.example.smartpest.api.RetrofitInstance
import com.example.smartpest.api.WeatherModel
import kotlinx.coroutines.launch
import java.io.IOException

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    private val apiKey = "18d62b6c14b64dce833181206242109"

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getData(city: String) {
        viewModelScope.launch {
            _weatherResult.value = NetworkResponse.Loading
            try {
                val response = weatherApi.getWeather(city, this@WeatherViewModel.apiKey)
                if (response.isSuccessful) {
                    _weatherResult.value = NetworkResponse.Success(response.body()!!)
                } else {
                    Log.e(
                        "WeatherViewModel",
                        "Failed to load data: ${response.errorBody()?.string()}"
                    )
                    _weatherResult.value =
                        NetworkResponse.Error("Failed to load data: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Failed to load data: ${e.localizedMessage}")
                _weatherResult.value = NetworkResponse.Error("Failed to load data: ${e.message}")
            } catch (e: HttpException) {
                Log.e("WeatherViewModel", "HTTP Exception: ${e.localizedMessage}")
                _weatherResult.value = NetworkResponse.Error("HTTP Exception: ${e.message}")
            } catch (e: IOException) {
                Log.e("WeatherViewModel", "IO Exception: ${e.localizedMessage}")
                _weatherResult.value = NetworkResponse.Error("IO Exception: ${e.message}")
            }
        }
    }
}