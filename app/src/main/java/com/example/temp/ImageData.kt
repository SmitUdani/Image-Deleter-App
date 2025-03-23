package com.example.temp

import android.net.Uri


data class ImageData(
    val imageId: Long,
    val monthYear: String,
    val uri: Uri
)