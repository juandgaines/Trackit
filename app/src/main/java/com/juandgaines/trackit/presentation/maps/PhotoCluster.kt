package com.juandgaines.trackit.presentation.maps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.juandgaines.trackit.domain.location.LocationWithTimestamp
import com.juandgaines.trackit.presentation.camera.toBitmap

/**
 * Displays a photo cluster marker on the map for locations with photos
 */
@Composable
fun PhotoCluster(
    locationWithPhotos: LocationWithTimestamp,
) {
    val photoCount = remember (locationWithPhotos){
        locationWithPhotos.listPhotos.size
    }
    
    if (photoCount > 0) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ) {
                    Text(text = photoCount.toString())
                }
            },
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Show thumbnail of first photo if available
                if (locationWithPhotos.listPhotos.isNotEmpty()) {
                    val firstPhoto = locationWithPhotos.listPhotos.first()
                    if (firstPhoto.exists() && firstPhoto.canRead()) {

                        val bitmap = firstPhoto.readBytes().toBitmap()
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Photo thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                    else {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Photos",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                } else {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Photos",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

