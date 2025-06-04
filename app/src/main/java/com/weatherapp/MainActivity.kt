package com.weatherapp

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.weatherapp.ui.WeatherViewModel
import com.weatherapp.ui.theme.screens.WeatherScreen
import com.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    // Sijaintilupien pyyntö
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("MainActivity", "Location permissions result: $permissions")
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Tarkka sijainti myönnetty
                Log.d("MainActivity", "Precise location permission granted")
                viewModel.refreshWeather() // Käyttää sijaintia
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Karkea sijainti myönnetty
                Log.d("MainActivity", "Coarse location permission granted")
                viewModel.refreshWeather() // Käyttää sijaintia (vaikka karkea)
            }
            else -> {
                // Lupaa ei myönnetty, käytä oletussijaintia (Helsinki)
                Log.d("MainActivity", "Location permission denied, using default")
                viewModel.fetchWeather(60.1699, 24.9384)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pyydä sijaintiluvat
        requestLocationPermission()

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen(viewModel)
                }
            }
        }
    }

    private fun requestLocationPermission() {
        Log.d("MainActivity", "Requesting location permissions")
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}