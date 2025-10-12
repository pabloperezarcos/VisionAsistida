@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.visionasistida.ui.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HelpScreen(navController: NavController? = null) {
    var showUso by remember { mutableStateOf(false) }
    var showAccesibilidad by remember { mutableStateOf(false) }
    var showControles by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda y Tutorial de Uso") },
                navigationIcon = {
                    if (navController != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sección 1
            Card {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("¿Cómo usar VisionAsistida?")
                        TextButton(onClick = { showUso = !showUso }) {
                            Text(if (showUso) "Ocultar" else "Mostrar")
                        }
                    }
                    AnimatedVisibility(
                        visible = showUso,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(" Inicia sesión con tu correo y contraseña registrados.")
                            Text(" En el Home podrás leer por voz el resumen y acceder a la ubicación.")
                            Text(" Desde 'Ubicación' puedes obtener tus coordenadas actuales.")
                            Text(" Usa el botón 'Cerrar sesión' para salir de forma segura.")
                        }
                    }
                }
            }

            // Sección 2
            Card {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Consejos de accesibilidad")
                        TextButton(onClick = { showAccesibilidad = !showAccesibilidad }) {
                            Text(if (showAccesibilidad) "Ocultar" else "Mostrar")
                        }
                    }
                    AnimatedVisibility(
                        visible = showAccesibilidad,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(" Activa TalkBack desde Ajustes → Accesibilidad → TalkBack.")
                            Text(" Usa doble toque para activar elementos enfocados.")
                            Text(" Ajusta el tamaño de fuente y el contraste desde la configuración del sistema.")
                        }
                    }
                }
            }

            // Sección 3
            Card {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Controles de la aplicación")
                        TextButton(onClick = { showControles = !showControles }) {
                            Text(if (showControles) "Ocultar" else "Mostrar")
                        }
                    }
                    AnimatedVisibility(
                        visible = showControles,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(" Botón 'Leer': reproduce por voz un resumen de la pantalla actual.")
                            Text(" Botón 'Abrir ubicación': muestra tu posición en el mapa OSM.")
                            Text(" Sección 'Usuarios registrados': permite eliminar usuarios si eres administrador.")
                        }
                    }
                }
            }
        }
    }
}
