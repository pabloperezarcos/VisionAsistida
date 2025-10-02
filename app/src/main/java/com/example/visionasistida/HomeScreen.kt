@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.visionasistida

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.visionasistida.data.UserStore

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "¡Bienvenido a VisionAsistida!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(12.dp))

        Text(
            text = "Usuarios registrados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        // "Tabla" simple con encabezados y filas
        HeaderRow()
        Divider()

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(UserStore.users) { user ->
                UserRow(email = user.email)
                Divider()
            }
            // Si no hay usuarios, muestra contenido base
            if (UserStore.users.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay usuarios registrados. Puedes crear uno desde la pantalla Registro.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Componentes requeridos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        ComponentsDemoSection()
    }
}

@Composable
private fun ComponentsDemoSection() {
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


@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Email", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Estado", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelLarge)
        Text("Acción", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun UserRow(email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(email, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text("Activo", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
        Text("Ver", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
    }
}
