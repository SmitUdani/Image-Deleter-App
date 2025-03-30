package com.example.temp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun ActionButtonRow(
    swipeRightButtonHandler: () -> Unit,
    swipeLeftButtonHandler: () -> Unit,
    goBackButtonHandler: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        ActionButton(Icons.Default.Close, swipeLeftButtonHandler)

        ActionButton(Icons.Default.Refresh, goBackButtonHandler)

        ActionButton(Icons.Default.Check, swipeRightButtonHandler)

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