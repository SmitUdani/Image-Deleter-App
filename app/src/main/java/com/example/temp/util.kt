package com.example.temp

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.dp
import com.spartapps.swipeablecards.ui.SwipeableCardsProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

suspend fun fetchImages(context: Context) =
    withContext(Dispatchers.IO) {
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

                images.add(ImageData(id, monthYear, contentUri))

                if(images.size > 50) break
            }
        }
        images.toList()
    }

val swipeCardProperties = SwipeableCardsProperties(
    stackedCardsOffset = 0.dp,
    swipeThreshold = 30.dp
)

fun deleteMediaDefault(contentResolver: ContentResolver, uris: List<Uri>) {
    for(uri in uris)
        contentResolver.delete(uri, null, null)
}