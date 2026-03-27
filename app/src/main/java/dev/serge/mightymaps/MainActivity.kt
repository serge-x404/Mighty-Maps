package dev.serge.mightymaps

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val home = LatLng(20.95,72.92)

            val cameraPositonState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
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
                mutableStateOf(
                    MapType.NORMAL
                )
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

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),

                cameraPositionState = rememberCameraPositionState {
                    position = testingTiltBearing
                },
                properties = MapProperties(mapType = mapType)
            ) {
                Marker(
                    state = MarkerState(home),
                    title = "India",
                    snippet = "Marker @ (23.05,72.50)",
                    alpha = 1f,
                    onClick = { marker ->
                        Log.v("TAGY","You clicked India")
                        true    // false -> to show marker snippet
                    }
                )
                Polyline(
                    points = routePoints,
                    clickable = true,
                    width = 10f,
                    color = Color.Red,
                    onClick = {
                        Log.v("TAGY","You clicked polyline")
                    },
                    geodesic = true
                )

                val trianglePoints = listOf(
                    LatLng(34.0522,-118.2437), // LA
                    LatLng(37.7749,-122.4194), // SF
                    LatLng(32.7157,-117.1611), // SD
                )


                Polygon(
                    points = trianglePoints,
                    fillColor = Color.Yellow,
                    strokeColor = Color.Red,
                    strokeWidth = 7f,
                    clickable = true,
                    onClick = {
                        Log.v("TAGY","Polygon is clicked")
                    }
                )

            }
        }
    }
}