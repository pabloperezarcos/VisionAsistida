@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.visionasistida.ui.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
fun LocationScreen() {
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasPermission by remember { mutableStateOf(false) }
    var lat by remember { mutableStateOf(-33.4489) }   // Santiago (fallback)
    var lon by remember { mutableStateOf(-70.6693) }
    var result by remember { mutableStateOf("Ubicación no solicitada aún.") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms -> hasPermission = perms.values.all { it } }

    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        hasPermission = fine == PackageManager.PERMISSION_GRANTED && coarse == PackageManager.PERMISSION_GRANTED
        if (!hasPermission && context is Activity) {
            launcher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
        // OSM necesita userAgent
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ubicación (OpenStreetMap)") }) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // 👈 permite scroll y evita que el mapa “pise” textos
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {
                if (!hasPermission) {
                    result = "Permisos denegados."
                    return@Button
                }
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            lat = loc.latitude
                            lon = loc.longitude
                            result = "Ubicación actual: lat=${"%.5f".format(lat)}, lon=${"%.5f".format(lon)}"
                        } else {
                            result = "No se pudo obtener ubicación actual."
                        }
                    }
                    .addOnFailureListener { e ->
                        result = "Error: ${e.message}"
                    }
            }) { Text("Obtener ubicación") }

            Text(result)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                AndroidView<MapView>(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx: Context ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            controller.setZoom(15.0)
                            controller.setCenter(GeoPoint(lat, lon))
                            setMultiTouchControls(true)
                        }
                    },
                    update = { map: MapView ->
                        val point = GeoPoint(lat, lon)
                        // Marcador simple en la ubicación actual
                        map.overlays.clear()
                        val marker = Marker(map).apply {
                            position = point
                            title = "Tu ubicación"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        map.overlays.add(marker)
                        map.controller.setCenter(point)
                        map.invalidate()
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}