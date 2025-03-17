package com.example.temp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.spartapps.swipeablecards.state.SwipeableCardsState
import com.spartapps.swipeablecards.state.rememberSwipeableCardsState
import com.spartapps.swipeablecards.ui.SwipeableCardDirection
import com.spartapps.swipeablecards.ui.SwipeableCardsProperties
import com.spartapps.swipeablecards.ui.lazy.LazySwipeableCards
import com.spartapps.swipeablecards.ui.lazy.items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen() {

    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }



    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )

    if(!hasPermission) {
        LaunchedEffect(Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }

    else CardStack()


}


@Composable
fun CardStack() {
    var imageUris by remember { mutableStateOf( listOf<Uri>() ) }
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false)}
    val state = rememberSwipeableCardsState(itemCount = { imageUris.size })

    LaunchedEffect(Unit) {
        loading = true

        println("Images are fetching")

        val fetchedImages = withContext(Dispatchers.IO) {
            fetchImages(context)
        }

        imageUris = fetchedImages
        loading = false

        println("fetching completed")
    }

    Column(
        modifier = Modifier.padding(10.dp)
            .fillMaxSize()
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        LazySwipeableCards(
            modifier = Modifier.fillMaxWidth().height(700.dp),
            state = state,
            onSwipe = { profile, direction ->
                when (direction) {
                    SwipeableCardDirection.Right -> {
//                        Toast.makeText(context, "Kept", Toast.LENGTH_SHORT).show()
                    }

                    SwipeableCardDirection.Left -> {
//                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            properties = SwipeableCardsProperties(
                stackedCardsOffset = 0.dp,
                swipeThreshold = 30.dp
            )
        ) {
            items(imageUris) { profile, index, offset ->
                Card {
                    AsyncImage(
                        model = profile,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        ActionButtonRow(state, context)

    }
}

@Composable
fun ActionButtonRow(state: SwipeableCardsState, context: Context) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        ActionButton(Icons.Default.Close) {
            state.swipe(SwipeableCardDirection.Left)
//            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
        }

        ActionButton(Icons.Default.Refresh) {
            state.goBack()
        }

        ActionButton(Icons.Default.Check) {
            state.swipe(SwipeableCardDirection.Right)
//            Toast.makeText(context, "Kept", Toast.LENGTH_SHORT).show()
        }

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

