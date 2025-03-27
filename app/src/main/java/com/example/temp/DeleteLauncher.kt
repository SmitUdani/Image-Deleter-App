package com.example.temp

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun DeleteLauncher(
    activity: MainActivity,
    deleteUriList: List<Uri>
) {
    LaunchedEffect(Unit) {
        deleteImages(activity, deleteUriList)

    }
}