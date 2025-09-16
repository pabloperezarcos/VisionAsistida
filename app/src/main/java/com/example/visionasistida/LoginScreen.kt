@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.visionasistida

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.visionasistida.data.UserStore
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val haptics = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailTouched by remember { mutableStateOf(false) }
    var passTouched by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_aa),
                contentDescription = "Logo de la aplicación VisionAsistida",
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 16.dp)
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailTouched = true
                },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailTouched && !isValidEmail(email),
                supportingText = {
                    if (emailTouched && !isValidEmail(email)) {
                        Text("Ingresa un correo válido")
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passTouched = true
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        val cd = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        Icon(painter = icon, contentDescription = cd)
                    }
                },
                isError = passTouched && password.isBlank(),
                supportingText = {
                    if (passTouched && password.isBlank()) {
                        Text("La contraseña no puede estar vacía")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Botón Iniciar Sesión
            Button(
                onClick = {

                    if (!isValidEmail(email) || password.isBlank()) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        emailTouched = true
                        passTouched = true
                        showSnack(scope, snackbarHostState, "Revisa el correo y la contraseña")
                        return@Button
                    }

                    val ok = UserStore.validateLogin(email, password)
                    if (ok) {
                        // Limpiar campos y navegar sin volver atrás a Login
                        email = ""
                        password = ""
                        showSnack(scope, snackbarHostState, "Bienvenido/a")
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        showSnack(scope, snackbarHostState, "Correo o contraseña inválidos")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión", fontSize = 20.sp)
            }

            Spacer(Modifier.height(8.dp))

            // Botón Crear cuenta
            TextButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta", fontSize = 18.sp)
            }

            // Botón Recuperar contraseña
            TextButton(
                onClick = { navController.navigate("forgot_password") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recuperar contraseña", fontSize = 18.sp)
            }
        }
    }
}

private fun isValidEmail(email: String): Boolean =
    email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

// Utilidad para mostrar snackbars desde eventos onClick
private fun showSnack(scope: CoroutineScope, host: SnackbarHostState, message: String) {
    scope.launch {
        host.currentSnackbarData?.dismiss()
        host.showSnackbar(message)
    }
}
