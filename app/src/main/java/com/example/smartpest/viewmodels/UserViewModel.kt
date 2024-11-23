package com.example.smartpest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpest.database.User
import com.example.smartpest.database.UserDao
import com.example.smartpest.database.UserEvent
import com.example.smartpest.database.UserState
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

    private fun loadUser() {
        viewModelScope.launch {
            try {
                userDao.getUser()?.let { user ->
                    _state.update { it.copy(
                        name = user.name,
                        location = user.location,
                        phone = user.phone,
                        language = user.language
                    ) }
                }
            } catch (e: Exception) {
                // Handle error if needed
                // You might want to add error handling state
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
                    id = 0,
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