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
fun RegisterScreen(navController: NavController) {
    val haptics = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailTouched by remember { mutableStateOf(false) }
    var passTouched by remember { mutableStateOf(false) }
    var confirmTouched by remember { mutableStateOf(false) }

    var passVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

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
            Text(text = "Registro", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))

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

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passTouched = true
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passVisible)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    IconButton(onClick = { passVisible = !passVisible }) {
                        val cd = if (passVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        Icon(painter = icon, contentDescription = cd)
                    }
                },
                isError = passTouched && !isValidPassword(password),
                supportingText = {
                    if (passTouched && !isValidPassword(password)) {
                        Text("Mínimo 6 caracteres")
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            // Confirmación
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmTouched = true
                },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (confirmVisible)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        val cd = if (confirmVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        Icon(painter = icon, contentDescription = cd)
                    }
                },
                isError = confirmTouched && confirmPassword != password,
                supportingText = {
                    if (confirmTouched && confirmPassword != password) {
                        Text("Las contraseñas no coinciden")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validación de campos
                    val emailOk = isValidEmail(email)
                    val passOk = isValidPassword(password)
                    val confirmOk = confirmPassword == password

                    if (!emailOk || !passOk || !confirmOk) {
                        emailTouched = true; passTouched = true; confirmTouched = true
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        showSnack(scope, snackbarHostState, "Revisa los campos del formulario")
                        return@Button
                    }

                    when (UserStore.addUser(email, password)) {
                        UserStore.AddResult.OK -> {
                            // Limpia y vuelve al login sin dejar rastro en back stack
                            email = ""; password = ""; confirmPassword = ""
                            showSnack(scope, snackbarHostState, "Usuario registrado. Inicia sesión.")
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        UserStore.AddResult.LIMIT_REACHED -> {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            showSnack(scope, snackbarHostState, "Se alcanzó el límite de 5 usuarios")
                        }
                        UserStore.AddResult.DUPLICATE -> {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            showSnack(scope, snackbarHostState, "El correo ya está registrado")
                        }
                        UserStore.AddResult.INVALID -> {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            showSnack(scope, snackbarHostState, "Correo o contraseña inválidos")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Registrarse", fontSize = 20.sp)
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión aquí", fontSize = 18.sp)
            }
        }
    }
}

private fun isValidEmail(email: String): Boolean =
    email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

private fun isValidPassword(password: String): Boolean = password.length >= 6

// Utilidad para mostrar snackbars desde eventos onClick
private fun showSnack(scope: CoroutineScope, host: SnackbarHostState, message: String) {
    scope.launch {
        host.currentSnackbarData?.dismiss()
        host.showSnackbar(message)
    }
}
