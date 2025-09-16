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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.visionasistida.data.UserStore

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val haptics = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var emailTouched by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SmallTopAppBar(title = { Text("Recuperar contraseña") })
        }
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
                    .size(160.dp)
                    .padding(bottom = 16.dp)
            )

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

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validación de formato
                    if (!isValidEmail(email)) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        emailTouched = true
                        showSnackbar(snackbarHostState, "Correo no válido")
                        return@Button
                    }

                    // ¿Existe el correo en nuestra lista local?
                    val exists = UserStore.users.any { it.email.equals(email.trim(), true) }

                    if (exists) {
                        // Simulación de envío de correo
                        showSnackbar(snackbarHostState, "Correo de recuperación enviado")
                    } else {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        showSnackbar(snackbarHostState, "El correo no está registrado")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Enviar", fontSize = 20.sp)
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a iniciar sesión", fontSize = 18.sp)
            }
        }
    }
}

private fun isValidEmail(email: String): Boolean =
    email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

private suspend fun SnackbarHostState.safeShow(message: String) {
    currentSnackbarData?.dismiss()
    showSnackbar(message)
}

private fun showSnackbar(host: SnackbarHostState, message: String) {
    androidx.compose.runtime.LaunchedEffect(message) {
        host.safeShow(message)
    }
}
