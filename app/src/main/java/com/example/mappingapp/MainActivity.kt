package com.example.mappingapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mappingapp.ui.theme.MappingAppTheme
import android.Manifest
import android.app.Notification
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre

//data class LatLng(var latitude: Double, var longitude: Double)

class MainActivity : ComponentActivity(), LocationListener {

    var styleBuilder = Style.Builder().fromUri ("https://tiles.openfreemap.org/styles/bright")
    val gpsViewModel : GPSViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        setContent {
            MappingAppTheme {
                val latLngState = remember { mutableStateOf(LatLng(0.0, 0.0)) }
                GPSDisplayer(latLngState.value) // imagine GPSDisplayer is our own composable
                gpsViewModel.latLngLiveData.observe(this) {
                    latLngState.value = it
                }
                GPSLatLngEnter()
                MapLibre(modifier = Modifier.fillMaxSize(),
                    styleBuilder = styleBuilder,
                    cameraPosition = CameraPosition(
                        target = latLngState.value,
                        zoom = 14.0
                    )
                )
            }
        }
    }

    // Checks whether GPS permission has been granted
    // If it has, start the GPS
    // If not, request permission from user
    fun checkPermissions() {
        val requiredPermission = Manifest.permission.ACCESS_FINE_LOCATION

        if(checkSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {
            startGPS()
        } else {
            val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if(isGranted) {
                    startGPS()
                } else {
                    // Permission not granted
                    Toast.makeText(this, "GPS permission not granted", Toast.LENGTH_LONG).show()
                }
            }
            permissionLauncher.launch(requiredPermission)
        }
    }

    @SuppressLint("MissingPermission")
    fun startGPS() {
        val mgr = getSystemService(LOCATION_SERVICE) as LocationManager
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this )
    }

    // Compulsory - provide onLocationChanged() method which runs whenever
    // the location changes
    override fun onLocationChanged(location: Location) {
        Toast.makeText(this, "Latitude: ${location.latitude}, Longitude: ${location.longitude}", Toast.LENGTH_SHORT).show()
        // gpsViewModel.latLng = LatLng(location.latitude, location.longitude)
    }

    // Optional - runs when the user enables the GPS
    override fun onProviderEnabled(provider: String) {
        Toast.makeText(this, "GPS enabled", Toast.LENGTH_LONG).show()

    }

    // Optional - runs when the user disables the GPS
    override fun onProviderDisabled(provider: String) {
        Toast.makeText(this, "GPS disabled", Toast.LENGTH_LONG).show()
    }

    @Composable
    fun GPSDisplayer(latLngState : LatLng) {
        // Composable to display lat and long
        Column {
            Text("Latitude : ${latLngState.latitude}")
            Text("Longitude : ${latLngState.longitude}")
        }
    }

    @Composable
    fun GPSLatLngEnter() {
        //var latLngState = remember { mutableStateOf(LatLng(0.0, 0.0)) }
        var lat = remember { mutableStateOf(0.0)}
        var lng = remember { mutableStateOf(0.0)}
        Column {
                Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    OutlinedTextField{modifier = Mofifier.weight(2f),
                        value = lng.value, onValueChange = {label = Text("Latitude : "))

                    TextField(label = {"Longitude : ${lng.value}"})
                    Button(onClick = {
                      //  latLngState.value.latitude = lat.value,
                     //   latLngState.value.longitude = lng.value
                        gpsViewModel.latLng = LatLng(lat.value, lng.value)
                    }, content={Text("Test")})

                }
        }

    }

}
