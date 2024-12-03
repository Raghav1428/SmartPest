package com.example.smartpest.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpest.database.alert.Alert
import com.example.smartpest.database.alert.AlertDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertViewModel(private val alertDao: AlertDao): ViewModel() {

    val allAlerts: LiveData<List<Alert>> = alertDao.getAllAlerts()

    fun deleteAlert(alertId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            alertDao.deleteAlert(alertId)
        }
    }

    fun deleteOldAlerts() {
        val fifteenDaysAgo = System.currentTimeMillis() - (15L * 24 * 60 * 60 * 1000)
        viewModelScope.launch(Dispatchers.IO) {
            alertDao.deleteOldAlerts(fifteenDaysAgo)
        }
    }

}