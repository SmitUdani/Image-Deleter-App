package com.example.temp

import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage


data class ImageData(
    val imageId: Long,
    val monthYear: String,
    val uri: Uri
)

@Composable
fun ImageCard(profile: ImageData) {

    var currContentScale by remember { mutableStateOf(ContentScale.Crop) }

    Card {
        AsyncImage(
            model = profile.uri,
            contentScale = currContentScale,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures (
                        onPress = {
                            currContentScale = ContentScale.Fit
                            awaitRelease()
                            currContentScale = ContentScale.Crop
                        }
                    )
                }
        )
    }
}