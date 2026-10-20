package com.example.smartpest.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.smartpest.ui.components.SectionLabel
import com.example.smartpest.ui.components.TomatoMark
import com.example.smartpest.viewmodels.AuthState
import com.example.smartpest.viewmodels.AuthViewModel

@Composable
fun LoginPage(navController: NavHostController, authViewModel: AuthViewModel) =
    AuthenticationPage(navController, authViewModel, isSignUp = false)

@Composable
internal fun AuthenticationPage(navController: NavHostController, authViewModel: AuthViewModel, isSignUp: Boolean) {
    val authState by authViewModel.authState.observeAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var resetDialog by rememberSaveable { mutableStateOf(false) }
    val loading = authState is AuthState.Loading
    val focus = LocalFocusManager.current
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate(AppRoutes.HOME) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    Box(Modifier.fillMaxSize().safeDrawingPadding().imePadding(), contentAlignment = Alignment.Center) {
        Column(Modifier.widthIn(max = 480.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TomatoMark(Modifier.size(36.dp))
                Text("TomatoCare", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(12.dp))
            SectionLabel(if (isSignUp) "GROW WITH A LITTLE GUIDANCE" else "BACK TO YOUR TOMATO PATCH")
            Text(if (isSignUp) "Let's get growing." else "Welcome back.", style = MaterialTheme.typography.headlineLarge)
            Text(if (isSignUp) "Create your account to get started with tomato leaf screening and care guidance."
                else "Sign in for tomato leaf screening, practical guidance and your grower profile.",
                style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                shape = MaterialTheme.shapes.medium)
            PasswordField(password, { password = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }))
            if (authState is AuthState.Error) {
                Text((authState as AuthState.Error).message, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error)
            }
            if (authState is AuthState.PasswordResetEmailSent) {
                Text("Password reset email sent. Check your inbox.", color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = {
                focus.clearFocus()
                if (isSignUp) authViewModel.signup(email.trim(), password) else authViewModel.login(email.trim(), password)
            }, enabled = !loading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                if (loading) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                }
                Text(if (loading) "Please wait…" else if (isSignUp) "Create account" else "Sign in")
            }
            if (!isSignUp) TextButton(onClick = { resetDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Forgot password?")
            }
            TextButton(onClick = {
                authViewModel.clearError()
                if (isSignUp) navController.popBackStack() else navController.navigate(AppRoutes.SIGNUP) { launchSingleTop = true }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (isSignUp) "Already have an account? Sign in" else "New here? Create an account")
            }
            Text("Leaf scanning runs on your device. The optional assistant and weather tools need an internet connection.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if (resetDialog) ForgotPasswordDialog({ resetDialog = false }, { authViewModel.resetPassword(it) })
}

@Composable
fun PasswordField(value: String, onValueChange: (String) -> Unit,
                  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
                  keyboardActions: KeyboardActions = KeyboardActions.Default, label: String = "Password") {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
        label = { Text(label) }, singleLine = true, shape = MaterialTheme.shapes.medium,
        keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    if (visible) "Hide password" else "Show password")
            }
        })
}

@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit, onResetPassword: (String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Reset password") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Enter your account email to receive a reset link.")
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        } },
        confirmButton = { TextButton(enabled = email.isNotBlank(), onClick = { onResetPassword(email.trim()); onDismiss() }) { Text("Send reset link") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
