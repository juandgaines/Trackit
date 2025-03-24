package com.juandgaines.trackit.presentation.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.juandgaines.trackit.R

@Composable
fun PermissionRationaleDialogs(
    showLocationRationale: Boolean,
    showNotificationRationale: Boolean,
    showCameraRationale: Boolean,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {


    if(showCameraRationale || showLocationRationale || showNotificationRationale){
        val title = when{
            showLocationRationale && showNotificationRationale -> stringResource(R.string.permissions_required)
            showLocationRationale -> stringResource(R.string.location_permission_required)
            showNotificationRationale -> stringResource(R.string.notification_permission_required)
            else -> stringResource(R.string.camera_permission_required)

        }

        val message = when{
            showLocationRationale && showNotificationRationale -> stringResource(R.string.location_and_notification_rationale)
            showLocationRationale -> stringResource(R.string.location_rationale)
            showNotificationRationale -> stringResource(R.string.notification_rationale)
            else -> stringResource(R.string.camera_rationale)
        }

        PermissionRationaleDialog(
            title = title,
            message = message,
            confirmButtonText = stringResource(R.string.grant_permissions),
            dismissButtonText = stringResource(R.string.not_now),
            onDismiss = {
                onDismiss()
            },
            onAccept = {
                onAccept()
            }
        )
    }
}

@Composable
private fun PermissionRationaleDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onAccept) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(dismissButtonText)
            }
        }
    )
}
