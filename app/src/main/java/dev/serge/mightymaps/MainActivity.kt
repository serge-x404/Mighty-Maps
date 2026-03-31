package dev.serge.mightymaps

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GroundOverlay
import com.google.maps.android.compose.GroundOverlayPosition
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.ScaleBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val context = LocalContext.current

            val home = LatLng(20.95,72.92)

            val cameraPositonState = rememberCameraPositionState {
                position = CameraPosition
                    .fromLatLngZoom(
                    home,
                    4f
                    )
            }

            val testingTiltBearing = remember {
                CameraPosition(
                    home, // google plex coordinates
                    4f,     // close-up view
                    0f,       // rotated 90 degrees
                    0f     // 30 degree angle view
                )
            }

            // Map Types
            // 1 NORMAL
            // 2 TERRAIN
            // 3 SATELLITE
            // 4 HYBRID

            var mapType by remember {
                mutableStateOf(MapType.NORMAL)
            }

//            val customMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)

//            val customMarker2 = BitmapFactory.decodeResource(
//                applicationContext.resources,
//                R.drawable.target
//            )

            val office = LatLng(20.94,72.92)
            val college = LatLng(23.03,75.50)
            val school = LatLng(23.04,72.50)

            val routePoints = listOf(office,college, school)

            val markers = remember { mutableStateListOf<LatLng>() }

            val longClickedMarkers = remember { mutableStateListOf<LatLng>() }

            val manhattanBounds = LatLngBounds(
                LatLng(40.7000,-74.0200),
                LatLng(40.7800, -73.9600)
            )

            val scope = rememberCoroutineScope()

            var selectedMarker by remember { mutableStateOf<LatLng?>(null) }


            var myLocation by remember { mutableStateOf<Location?>(null) }

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    fetchCurrentLocation(
                        context,
                        onLocationFetched = {location ->
                            myLocation = location
                        }
                    )
                }
            }

            LaunchedEffect(Unit) {
                locationPermissionLauncher
                    .launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            var searchQuery by remember { mutableStateOf("") }
            var location by remember { mutableStateOf<LatLng?>(null) }
            var address by remember { mutableStateOf("Search or tap on map") }
            var coroutineScope = rememberCoroutineScope()


            Box(modifier = Modifier
                .fillMaxSize()
            ) {

                GoogleMap(

                    uiSettings = MapUiSettings(
                        compassEnabled = true,
                        zoomGesturesEnabled = false,
                        myLocationButtonEnabled = true,
                        rotationGesturesEnabled = false,
                        tiltGesturesEnabled = true
                    ),

                    modifier = Modifier
                        .fillMaxSize(),

                    cameraPositionState = cameraPositonState,
                    properties = MapProperties(
                        mapType = mapType,
                        //                    latLngBoundsForCameraTarget = manhattanBounds,
                        //                    minZoomPreference = 12f,
                        //                    maxZoomPreference = 12f
                    ),

                    onMapClick = {
                        Log.i("TAGY", "You clicked $it")
                        markers.add(it)

                        selectedMarker = it


                        scope.launch {
                            cameraPositonState.animate(
                                CameraUpdateFactory.newLatLngZoom(it, 15f),
                                800
                            )

                            location = it
                            address = reverseGeocode(context, it) ?: "Unknown address"
                        }
                    },

                    onMapLongClick = { latlng ->
                        Log.i("TAGY", "You long clicked $latlng")

                        longClickedMarkers.add(latlng)

                        selectedMarker = latlng

                        scope.launch {
                            cameraPositonState.animate(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(
                                        latlng,
                                        14f,
                                        0f,
                                        0f
                                    )
                                ), 1000
                            )
                        }
                    }
                ) {

                    location?.let {locationLatLng ->
                        Marker(
                            state = MarkerState(locationLatLng),
                            title = address,
                            snippet = "Lat:${locationLatLng.latitude}" +
                                    "Long:${locationLatLng.longitude}"
                        )
                    }



                    myLocation?.let { currentLocation->
                        val currentLatLng = LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )

                        Marker(
                            state = MarkerState(currentLatLng),
                            title = "Current Location",
                            snippet = "You are here"
                        )

                        Circle(
                            center = currentLatLng,
                            radius = 200.0,
                            fillColor = Color.Blue.copy(alpha = 0.2f),
                            strokeColor = Color.White
                        )
                    }

                    markers.forEach { location ->
                        Marker(
                            state = MarkerState(location),
                            title = "Marker at $location",
                            onInfoWindowLongClick = { marker ->
                                markers.remove(marker.position)
                            },
                            onClick = {marker ->
                                selectedMarker = marker.position
                                true
                            }
                        )
                    }

                    longClickedMarkers.forEach { location ->
                        Marker(
                            state = MarkerState(location),
                            title = "Long-pressed marker at $location",
                            icon = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_MAGENTA
                            ),
                            onInfoWindowLongClick = { marker ->
                                longClickedMarkers.remove(marker.position)
                            },
                            onClick = {marker ->
                                selectedMarker = marker.position
                                true
                            }
                        )
                    }
                    //                Marker(
                    //                    state = MarkerState(home),
                    //                    title = "India",
                    //                    snippet = "Marker @ (23.05,72.50)",
                    //                    alpha = 1f,
                    //                    onClick = { marker ->
                    //                        Log.v("TAGY","You clicked Marker")
                    //                        true    // false -> to show marker snippet
                    //                    }
                    //                )
                    Polyline(
                        points = routePoints,
                        clickable = true,
                        width = 10f,
                        color = Color.Red,
                        onClick = {
                            Log.v("TAGY", "You clicked polyline")
                        },
                        geodesic = true
                    )

                    val trianglePoints = listOf(
                        LatLng(34.0522, -118.2437), // LA
                        LatLng(37.7749, -122.4194), // SF
                        LatLng(32.7157, -117.1611), // SD
                    )


                    Polygon(
                        points = trianglePoints,
                        fillColor = Color.Yellow,
                        strokeColor = Color.Red,
                        strokeWidth = 7f,
                        clickable = true,
                        onClick = {
                            Log.v("TAGY", "Polygon is clicked")
                        }
                    )


                    val positionOverlay = GroundOverlayPosition.create(manhattanBounds)

                    GroundOverlay(
                        position = positionOverlay,
                        image = BitmapDescriptorFactory.fromResource(
                            R.drawable.nyc
                        ),
                        transparency = 0.3f,
                        bearing = 30f
                    )

                }

                ScaleBar(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    cameraPositionState = cameraPositonState
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        address,
                        modifier = Modifier.padding(16.dp)
                    )

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Enter Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                geocodeAddress(context,searchQuery) {
                                    locationLatLng ->
                                    locationLatLng?.let {
                                        cameraPositonState.position = CameraPosition.fromLatLngZoom(it, 15f)

                                        coroutineScope.launch {
                                            address = reverseGeocode(context, it) ?: "Unknown address"
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Search")
                    }
                }


                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        IconToggleButton(
                            checked = mapType == MapType.NORMAL,
                            onCheckedChange = {checked ->
                                mapType = if (checked) MapType.NORMAL else MapType.SATELLITE
                            }
                        ) {
                            Icon(
                                if (mapType == MapType.SATELLITE) Icons.Default.Map
                                else Icons.Default.Warning,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {}    //getCurrentLocation
                        ) {
                            Icon(Icons.Default.LocationOn, null)
                        }

                        IconButton(
                            onClick = {
                                scope.launch {

                                    val newPosition = CameraPosition(
                                        LatLng(0.0,0.0),
                                        5f,
                                        0f,
                                        0f
                                    )

                                    cameraPositonState.animate(
                                        CameraUpdateFactory.newCameraPosition(newPosition),
                                        5000
                                    )
                                }
                            }
                        ) {
                            Icon( Icons.Default.Restore, null )
                        }
                    }
                }
            }

            selectedMarker?.let { position ->
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .widthIn(max = 250.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            "Selected marker details",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("Latitude: ${position.latitude}")
                        Text("Latitude: ${position.longitude}")

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                selectedMarker = null

                                if (markers.contains(position)) {
                                    markers.remove(position)
                                }
                                else {
                                    longClickedMarkers.remove(position)
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
    private fun fetchCurrentLocation(
        context: Context,
        onLocationFetched: (Location) -> Unit
    ) {

        val fusedLocationClient = LocationServices
            .getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).apply {
            setMinUpdateIntervalMillis(5000)
            setWaitForAccurateLocation(true)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                        myCurrentLocation -> onLocationFetched(myCurrentLocation)
                }
            }
        }

        try {
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location ->
//                    if (location != null) {
//                        onLocationFetched(location)
//                    }
//                }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
        catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun geocodeAddress(
        context: Context,
        address: String,
        onResult: (LatLng?) -> Unit
    ) {

        val geocoder = Geocoder(context)

        try {
            val addresses = geocoder.getFromLocationName(address,1)

            if (addresses?.isNotEmpty() == true) {
                val location = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)
                onResult(latLng)
            }
            else {
                onResult(null)
            }
        }
        catch (e: IOException) {
            e.printStackTrace()
            onResult(null)
        }
    }

    suspend fun reverseGeocode(context: Context, latLng: LatLng): String? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())

                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1)

                addresses?.firstOrNull()?.getAddressLine(0)
            }
            catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}