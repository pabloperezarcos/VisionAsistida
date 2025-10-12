@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.visionasistida

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.visionasistida.accessibility.TtsManager

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.visionasistida.users.UsersViewModel
import com.example.visionasistida.data.UserEntity

@Composable
fun HomeScreen(
    navController: NavController,
    isAdmin: Boolean = true,
    greetedName: String? = null //nombre opcional para saludar
) {
    val context = LocalContext.current
    val tts = remember { TtsManager(context) }
    DisposableEffect(Unit) { onDispose { tts.shutdown() } }

    val vm: UsersViewModel = viewModel()
    val users = vm.users.collectAsStateWithLifecycle()

    var toDelete by remember { mutableStateOf<UserEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
                actions = {
                    TextButton(onClick = {
                        val nombre = greetedName ?: "usuario"
                        val resumen = buildString {
                            append("Bienvenido, $nombre, a Vision Asistida. ")
                            append("Usuarios registrados: ${users.value.size}. ")
                            append("Controles disponibles: combo, casillas de verificación y opciones de radio.")
                        }
                        tts.speak(resumen)
                    }) { Text("Leer", color = MaterialTheme.colorScheme.onPrimary) }

                    TextButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    }) { Text("Cerrar sesión", color = MaterialTheme.colorScheme.onPrimary) }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Saludo (usa el nombre si viene)
            item {
                val nombre = greetedName ?: "Usuario"
                Text(
                    text = "¡Bienvenido, $nombre!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Navegación rápida
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("features/location") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Abrir Ubicación") }
            }

            // Sección: Usuarios
            item {
                Text(
                    text = "Usuarios registrados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                HeaderRow(showAction = isAdmin)
                Divider()
            }

            items(users.value, key = { it.id }) { user ->
                UserRow(
                    user = user,
                    showAction = isAdmin,
                    onDelete = { toDelete = user }
                )
                Divider()
            }

            if (users.value.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay usuarios registrados. Puedes crear uno desde la pantalla Registro.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            // Separador grande antes de “Componentes”
            item { Spacer(Modifier.height(16.dp)) }

            // Sección: Componentes requeridos
            item {
                Text(
                    text = "Componentes requeridos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                ComponentsDemoSection()
            }

            item {
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { navController.navigate("help") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Abrir Ayuda / Tutorial")
                }
            }

        }
    }

    // Diálogo de confirmación de eliminación
    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Eliminar al usuario ${toDelete!!.email}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteUser(toDelete!!.id)
                        toDelete = null
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Confirmar eliminación de usuario"
                    }
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(
                    onClick = { toDelete = null },
                    modifier = Modifier.semantics {
                        contentDescription = "Cancelar eliminación de usuario"
                    }
                ) { Text("Cancelar") }
            }
        )
    }
}

/* ----------------------------  Sección DEMO de componentes  ---------------------------- */

@Composable
private fun ComponentsDemoSection() {
    // COMBO (Exposed Dropdown)
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("Opción A", "Opción B", "Opción C")
    var seleccion by remember { mutableStateOf(opciones.first()) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text("Combo") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { op ->
                DropdownMenuItem(
                    text = { Text(op) },
                    onClick = {
                        seleccion = op
                        expanded = false
                    }
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // CHECKLIST
    var chkLecturaPorVoz by remember { mutableStateOf(true) }
    var chkAltoContraste by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = chkLecturaPorVoz, onCheckedChange = { chkLecturaPorVoz = it })
            Spacer(Modifier.width(8.dp))
            Text("Lectura por voz")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = chkAltoContraste, onCheckedChange = { chkAltoContraste = it })
            Spacer(Modifier.width(8.dp))
            Text("Alto contraste")
        }
    }

    Spacer(Modifier.height(16.dp))

    // RADIO BUTTONS
    var nivelAyuda by remember { mutableStateOf("Básico") }
    val niveles = listOf("Básico", "Intermedio", "Avanzado")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        niveles.forEach { etiqueta ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = nivelAyuda == etiqueta,
                    onClick = { nivelAyuda = etiqueta }
                )
                Spacer(Modifier.width(8.dp))
                Text(etiqueta)
            }
        }
    }
}

/* ----------------------------  Tabla (encabezados + filas)  ---------------------------- */

@Composable
private fun HeaderRow(showAction: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Email", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Estado", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelLarge)
        if (showAction) {
            Text("Acción", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun UserRow(
    user: UserEntity,
    showAction: Boolean,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(user.email, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text("Activo", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
        if (showAction) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .weight(0.5f)
                    .semantics { contentDescription = "Eliminar usuario ${user.email}" }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }
        }
    }
}
