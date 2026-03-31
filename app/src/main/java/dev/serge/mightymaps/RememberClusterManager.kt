package dev.serge.mightymaps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager

@Composable
fun rememberClusterManager(): ClusterManager<ClusterItem> {

    val context = LocalContext.current

    return remember { ClusterManager<ClusterItem>(context,null).apply {} }
}