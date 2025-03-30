package com.example.temp

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DeleteFiles(imagesUris: List<Uri>) {
    val contentResolver = LocalContext.current.contentResolver
    val android10 = Build.VERSION_CODES.Q
    var deleted by remember { mutableIntStateOf(200) }

    val permissionBuilder = when {
        Build.VERSION.SDK_INT > android10 -> {
            val pendingIntent = MediaStore.createDeleteRequest(contentResolver, imagesUris)

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { res -> deleted = res.resultCode }

            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

            permissionLauncher to intentSenderRequest
        }

        Build.VERSION.SDK_INT == android10 -> {
            try {
                deleteMediaDefault(contentResolver, imagesUris)
                deleted = Activity.RESULT_OK
                null to null

            } catch (exception: Exception) {
                if (exception is RecoverableSecurityException) {
                    val pendingIntent= exception.userAction.actionIntent

                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.StartIntentSenderForResult()
                    ) { res -> deleted = res.resultCode }

                    val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()

                    permissionLauncher to intentSenderRequest
                } else {
                    deleted = Activity.RESULT_CANCELED
                    null to null
                }
            }
        }

        else -> {
            DeleteMediaDefault(contentResolver, imagesUris)
            deleted = Activity.RESULT_OK
            null to null
        }
    }

    if(permissionBuilder.first != null) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                permissionBuilder.first!!.launch(permissionBuilder.second!!)
            }
        }
    }

    if(deleted == 200)
        DeletingAnimation()

    else if(deleted == Activity.RESULT_CANCELED)
        CancelledAnimation()

    else SuccessAnimation()
}

@Composable
fun DeleteMediaDefault(
    contentResolver: ContentResolver,
    imagesUris: List<Uri>
) {
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            for(uri in imagesUris)
                    contentResolver.delete(uri, null, null)
        }
    }
}