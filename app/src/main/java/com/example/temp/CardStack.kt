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
fun StateFullStack() {
    var images by remember { mutableStateOf( listOf<ImageData>() ) }
    val context = LocalContext.current
    val state = rememberSwipeableCardsState(itemCount = { images.size })

    LaunchedEffect(Unit) {
        images = fetchImages(context)
    }

    val directions = remember { mutableStateListOf<String>("") }
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

    val swipeRightButtonHandler = {
        if(images.isNotEmpty() && state.currentCardIndex != images.size) {
//            println("${state.currentCardIndex}")
            toKeep.add(images[state.currentCardIndex])
            state.swipe(SwipeableCardDirection.Right)
            directions.add("right")
        }
    }

    val swipeLeftButtonHandler = {
        if(images.isNotEmpty() && state.currentCardIndex != images.size) {
//            println("${state.currentCardIndex}")
            toDelete.add(images[state.currentCardIndex])
            state.swipe(SwipeableCardDirection.Left)
            directions.add("left")


        }
    }

    val goBackButtonHandler = {
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
        images, state,
        swipeRightHandler, swipeLeftHandler,
        swipeRightButtonHandler, swipeLeftButtonHandler,
        goBackButtonHandler, deleteUriList
    )

}

@Composable
fun StateLessStack(
    images: List<ImageData>,
    state: SwipeableCardsState,
    swipeRightHandler: (ImageData) -> Boolean,
    swipeLeftHandler: (ImageData) -> Boolean,
    swipeRightButtonHandler: () -> Unit,
    swipeLeftButtonHandler: () -> Unit,
    goBackButtonHandler: () -> Unit,
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

        ActionButtonRow(
            swipeRightButtonHandler,
            swipeLeftButtonHandler,
            goBackButtonHandler
        )
    }

    if(images.isNotEmpty() && state.currentCardIndex == images.size) {
        println("from delete ${state.currentCardIndex}")
        val imagesUri = deleteUriList()
        if(imagesUri.isNotEmpty())
            DeleteFiles(imagesUri)
        else SuccessAnimation()
    }
}