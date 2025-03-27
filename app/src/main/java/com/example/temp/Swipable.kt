package com.example.temp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.spartapps.swipeablecards.state.SwipeableCardsState
import com.spartapps.swipeablecards.state.rememberSwipeableCardsState
import com.spartapps.swipeablecards.ui.SwipeableCardDirection
import com.spartapps.swipeablecards.ui.lazy.LazySwipeableCards
import com.spartapps.swipeablecards.ui.lazy.items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(activity: MainActivity) {

    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var showPermissionButton by remember { mutableStateOf(true)  }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if(isGranted) {
                permissionGranted = true
                showPermissionButton = false
            } else {
                permissionGranted = false
                showPermissionButton = true
            }
        }
    )

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    permissionGranted = ContextCompat.checkSelfPermission(
        context,
        permissionToRequest
    ) == PackageManager.PERMISSION_GRANTED

    if(!permissionGranted) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(showPermissionButton) {
            Button(
                onClick = {
                    if(ContextCompat.checkSelfPermission(
                        context,
                        permissionToRequest
                    ) == PackageManager.PERMISSION_GRANTED) {
                        permissionGranted = true
                        showPermissionButton = false
                    } else {
                        permissionLauncher.launch(permissionToRequest)
                    }
                }
            ){
                Text("Grant Permission")
            }
        }
    } }

    else { StateFullStack(activity) }


}


@Composable
fun ActionButtonRow(onClick: () -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        ActionButton(Icons.Default.Refresh, onClick)

    }

}


@Composable
fun ActionButton(imageVec: ImageVector, onClick: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier.size(70.dp),
        onClick = onClick
    ) {
        Icon(
            imageVector = imageVec,
            contentDescription = null
        )
    }
}

