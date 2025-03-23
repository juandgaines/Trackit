package com.juandgaines.trackit.presentation.maps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.juandgaines.trackit.presentation.camera.toBitmap
import java.io.File
import kotlin.time.Duration

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGallery(
    photos: List<File>,
    locationTimestamp: Duration
) {

    val photosPoint = remember (photos) {
        photos
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            // Header with timestamp
            Text(
                text = "Photos at ${formatDuration(locationTimestamp)}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Photo pager
            if (photosPoint.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { photos.size })
                
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val photo = photos[page]
                    var showFullscreen by remember { mutableStateOf(false) }
                    
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showFullscreen = true }
                    ) {

                        val bitmap = photo.readBytes().toBitmap()
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Page indicator
                        Text(
                            text = "${page + 1}/${photos.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    RoundedCornerShape(topStart = 8.dp)
                                )
                                .padding(4.dp)
                        )
                    }

                    // Fullscreen dialog
                    if (showFullscreen) {
                        Dialog(onDismissRequest = { showFullscreen = false }) {
                            Box(modifier = Modifier.fillMaxSize()) {

                                val bitmap = photo.readBytes().toBitmap()
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Photo fullscreen",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )

                                IconButton(
                                    onClick = { showFullscreen = false },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No photos available",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatDuration(duration: Duration): String {
    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes % 60
    val seconds = duration.inWholeSeconds % 60
    
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
} 