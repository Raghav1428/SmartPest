package com.example.smartpest.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smartpest.database.user.UserEvent
import com.example.smartpest.viewmodels.AuthState
import com.example.smartpest.viewmodels.AuthViewModel
import com.example.smartpest.viewmodels.UserViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfilePage(authViewModel: AuthViewModel, userViewModel: UserViewModel) {
    val state by userViewModel.state.collectAsState()

    val languages = listOf("English", "Spanish", "French", "German", "Hindi")
    var isChanged by remember { mutableStateOf(false) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.PasswordResetEmailSent -> {
                Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
            }

            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT)
                    .show()
            }

            else -> Unit
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name Field
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    userViewModel.onEvent(UserEvent.SetName(it))
                    isChanged = true
                },
                label = { Text("Name") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Location Field
            OutlinedTextField(
                value = state.location,
                onValueChange = {
                    userViewModel.onEvent(UserEvent.SetLocation(it))
                    isChanged = true
                },
                label = { Text("Location") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Phone Field
            OutlinedTextField(
                value = state.phone,
                onValueChange = {
                    userViewModel.onEvent(UserEvent.SetPhone(it))
                    isChanged = true
                },
                label = { Text("Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Language Dropdown
            DropdownMenuField(
                selectedItem = state.language,
                items = languages,
                onItemSelected = {
                    userViewModel.onEvent(UserEvent.SetLanguage(it))
                    isChanged = true
                }
            )

            // Save Button
            Button(
                onClick = {
                    userViewModel.onEvent(UserEvent.SaveUser)
                    Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
                    isChanged = false
                },
                enabled = isChanged,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Save")
            }

            // Reset Password Button
            TextButton(
                onClick = { showForgotPasswordDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset Password", color = MaterialTheme.colorScheme.primary)
            }

            // Privacy Policy
            val uriHandler = LocalUriHandler.current
            TextButton(
                onClick = { uriHandler.openUri("https://www.google.com") }
            ) {
                Text("Privacy Policy")
            }

            if (showForgotPasswordDialog) {
                ForgotPasswordDialog(
                    onDismiss = { showForgotPasswordDialog = false },
                    onResetPassword = { email ->
                        authViewModel.resetPassword(email)
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuField(
    selectedItem: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldWidth = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text("Language") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldWidth.value = coordinates.size.width
                }
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldWidth.value.toDp() })
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
