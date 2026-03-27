package dev.serge.mightymaps

import android.graphics.BitmapFactory
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val india = LatLng(23.05,72.50)

            val cameraPositonState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    india,
                    4f
                )
            }

            val testingTiltBearing = remember {
                CameraPosition(
                    india, // google plex coordinates
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
                    MapType.SATELLITE
                )
            }

//            val customMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)

//            val customMarker2 = BitmapFactory.decodeResource(
//                applicationContext.resources,
//                R.drawable.target
//            )

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),

                cameraPositionState = rememberCameraPositionState {
                    position = testingTiltBearing
                },
                properties = MapProperties(mapType = mapType)
            ) {
                Marker(
                    state = MarkerState(india),
                    title = "India",
                    snippet = "Marker @ (23.05,72.50)",
                    alpha = 1f,
                    onClick = { marker ->
                        Log.v("TAGY","You clicked India")
                        true    // false -> to show marker snippet
                    }
                )
            }
        }
    }
}