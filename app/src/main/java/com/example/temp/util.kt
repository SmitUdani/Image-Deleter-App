package com.example.temp

import android.content.ContentUris
import android.content.Context
import android.icu.util.Calendar
import android.media.Image
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.unit.dp
import com.spartapps.swipeablecards.ui.SwipeableCardDirection
import com.spartapps.swipeablecards.ui.SwipeableCardsProperties
import java.text.SimpleDateFormat
import java.util.Locale

fun fetchImages(context: Context): List<ImageData> {
    val images = mutableListOf<ImageData>()
    val contentResolver = context.contentResolver

    // The collection to query depends on the Android version.
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATE_ADDED
    )
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    contentResolver.query(
        collection,
        projection,
        null,
        null,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateTaken = cursor.getLong(dateTakenColumn)
            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            val calender = Calendar.getInstance()
            calender.timeInMillis = dateTaken

            val monthYearFormater = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            val monthYear = monthYearFormater.format(calender.time)

//            println("$monthYear -> $contentUri")

            images.add(ImageData(id, monthYear, contentUri))

            if(images.size > 50) break
        }
    }
    return images.toList()
}

val handleSwipe: (ImageData, SwipeableCardDirection) -> Unit = {  image, direction ->
    when (direction) {
        SwipeableCardDirection.Right -> {
//            Toast.makeText(context, "Kept", Toast.LENGTH_SHORT).show()
        }

        SwipeableCardDirection.Left -> {
//            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
        }
    }
}

val swipeCardProperties = SwipeableCardsProperties(
    stackedCardsOffset = 0.dp,
    swipeThreshold = 30.dp
)