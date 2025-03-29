package com.example.temp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun StateFullPermissionLauncher(activity: MainActivity) {

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

    val onClickHandler = {
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

    if(!permissionGranted)
        StateLessPermissionLauncher(showPermissionButton, onClickHandler)

    else StateFullStack(activity)
}

@Composable
fun StateLessPermissionLauncher(
    showPermissionButton: Boolean,
    onClickHandler: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(showPermissionButton) {
            Button(
                onClick = onClickHandler
            ) {
                Text("Grant Permission")
            }
        }
    }
}