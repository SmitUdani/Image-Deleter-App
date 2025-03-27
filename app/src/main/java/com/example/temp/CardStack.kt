package com.example.temp

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.spartapps.swipeablecards.state.SwipeableCardsState
import com.spartapps.swipeablecards.state.rememberSwipeableCardsState
import com.spartapps.swipeablecards.ui.SwipeableCardDirection
import com.spartapps.swipeablecards.ui.lazy.LazySwipeableCards
import com.spartapps.swipeablecards.ui.lazy.items


@Composable
fun StateFullStack(activity: MainActivity) {
    var images by remember { mutableStateOf( listOf<ImageData>() ) }
    val context = LocalContext.current
    val state = rememberSwipeableCardsState(itemCount = { images.size })

    LaunchedEffect(Unit) {
        images = fetchImages(context)
    }

    val directions = remember { mutableStateListOf<String>() }
    val toDelete = remember { mutableStateListOf<ImageData>() }
    val toKeep = remember { mutableStateListOf<ImageData>() }

    val swipeRightHandler = { image: ImageData ->
        directions.add("right")
        toKeep.add(image)
    }

    val swipeLeftHandler = { image: ImageData ->
        directions.add("left")
        toDelete.add(image)
    }

    val goBackHandler = {
        if(state.canSwipeBack) {
            state.goBack()
            val lastDirection = directions.removeAt(directions.lastIndex)
            if(lastDirection == "left")
                toDelete.removeAt(toDelete.lastIndex)
            else toKeep.removeAt(toKeep.lastIndex)
        }
    }

    val deleteUriList = { toDelete.map { image -> image.uri }}

    StateLessStack(
        activity, images, state,
        swipeRightHandler, swipeLeftHandler,
        goBackHandler, deleteUriList
    )

}

@Composable
fun StateLessStack(
    activity: MainActivity,
    images: List<ImageData>,
    state: SwipeableCardsState,
    swipeRightHandler: (ImageData) -> Boolean,
    swipeLeftHandler: (ImageData) -> Boolean,
    goBackHandler: () -> Unit,
    deleteUriList: () -> List<Uri>
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val reqHeight = screenHeight - screenHeight / 3

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(10.dp)
            .fillMaxSize(),

        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(35.dp))

        LazySwipeableCards<ImageData>(
            modifier = Modifier.fillMaxWidth().height(reqHeight),
            properties = swipeCardProperties,
            state = state,
            onSwipe = { image, direction ->
                when (direction) {
                    SwipeableCardDirection.Right -> { swipeRightHandler(image) }
                    SwipeableCardDirection.Left -> { swipeLeftHandler(image) }
                }
            }
        ) {
            items(images) { profile, _, _ ->
                ImageCard(profile)
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        ActionButtonRow(goBackHandler)
    }

    if(images.isNotEmpty() && state.currentCardIndex == images.size) {
        DeleteLauncher(activity, deleteUriList())
    }
}