package com.example.smartpest.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUser(user: User)

    @Query("SELECT * FROM user WHERE id = 1")
    suspend fun getUser(): User?

}