package com.example.smartpest.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpest.database.user.User
import com.example.smartpest.database.user.UserDao
import com.example.smartpest.database.user.UserEvent
import com.example.smartpest.database.user.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(private val userDao: UserDao): ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    init {
        loadUser()
    }

    fun isProfileComplete(): Boolean {
        return state.value.name.trim().isNotBlank() &&
                state.value.location.trim().isNotBlank() &&
                state.value.phone.trim().isNotBlank()
    }
    private fun loadUser() {
        viewModelScope.launch {
            try {
                userDao.getUser()?.let { user ->
                    _state.update {
                        it.copy(
                            id = user.id,
                            name = user.name,
                            location = user.location,
                            phone = user.phone,
                            language = user.language
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", e.printStackTrace().toString())
            }
        }
    }

    fun onEvent(event: UserEvent) {
        when(event) {
            UserEvent.SaveUser -> {
                val currentState = state.value

                if(currentState.name.isBlank() ||
                    currentState.location.isBlank() ||
                    currentState.phone.isBlank() ||
                    currentState.language.isBlank()) {
                    return
                }

                val user = User(
                    id = currentState.id,
                    name = currentState.name,
                    location = currentState.location,
                    phone = currentState.phone,
                    language = currentState.language
                )

                viewModelScope.launch {
                    try {
                        userDao.upsertUser(user)
                    } catch (e: Exception) {
                        // Handle error if needed
                    }
                }

            }
            is UserEvent.SetLanguage -> {
                _state.update { it.copy(
                    language = event.language
                ) }
            }
            is UserEvent.SetLocation -> {
                _state.update { it.copy(
                    location = event.location
                ) }
            }
            is UserEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is UserEvent.SetPhone -> {
                _state.update { it.copy(
                    phone = event.phone
                ) }

            }
        }
    }
}