package com.example.smartpest.database.alert

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlertDao {

    @Insert
    suspend fun insertAlert(alert: Alert)

    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): LiveData<List<Alert>>

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteAlert(id: Int)

    @Query("DELETE FROM alerts WHERE timestamp < :thresholdTime")
    suspend fun deleteOldAlerts(thresholdTime: Long)

}