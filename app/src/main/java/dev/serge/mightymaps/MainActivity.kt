package dev.serge.mightymaps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val india = LatLng(23.5,80.00)

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
                    90f,       // rotated 90 degrees
                    30f     // 30 degree angle view
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),

                cameraPositionState = rememberCameraPositionState {
                    position = testingTiltBearing
                }
            )

        }
    }
}