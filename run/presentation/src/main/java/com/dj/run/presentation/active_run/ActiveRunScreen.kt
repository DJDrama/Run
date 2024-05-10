@file:OptIn(ExperimentalMaterial3Api::class)

package com.dj.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dj.core.presentation.designsystem.RunTheme
import com.dj.core.presentation.designsystem.StartIcon
import com.dj.core.presentation.designsystem.StopIcon
import com.dj.core.presentation.designsystem.components.RunActionButton
import com.dj.core.presentation.designsystem.components.RunOutlinedActionButton
import com.dj.core.presentation.designsystem.components.RunrunDialog
import com.dj.core.presentation.designsystem.components.RunrunFloatingActionButton
import com.dj.core.presentation.designsystem.components.RunrunScaffold
import com.dj.core.presentation.designsystem.components.RunrunToolbar
import com.dj.run.presentation.R
import com.dj.run.presentation.active_run.components.RunDataCard
import com.dj.run.presentation.active_run.maps.TrackerMap
import com.dj.run.presentation.active_run.service.ActiveRunService
import com.dj.run.presentation.util.hasLocationPermission
import com.dj.run.presentation.util.hasNotificationPermission
import com.dj.run.presentation.util.shouldShowLocationPermissionRationale
import com.dj.run.presentation.util.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream

@Composable
fun ActiveRunScreenRoot(
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel()
) {
    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val hasCourseLocationPermission = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasFineLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= 33) {
            perms[Manifest.permission.POST_NOTIFICATIONS] == true
        } else {
            true
        }
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCourseLocationPermission && hasFineLocationPermission,
                showLocationRationale = showLocationRationale
            )
        )

        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotificationPermission,
                showNotificationRationale = showNotificationRationale
            )
        )
    }

    // when visit the screen first time
    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationRationale = showLocationRationale
            )
        )

        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )

        if (!showLocationRationale && !showNotificationRationale) {
            permissionLauncher.requestRunrunPermissions(context)
        }
    }
    LaunchedEffect(key1 = state.isRunFinished) {
        if (state.isRunFinished) {
            onServiceToggle(false)
        }
    }
    LaunchedEffect(key1 = state.shouldTrack) {
        if (context.hasLocationPermission() && state.shouldTrack && !ActiveRunService.isServiceActive) {
            onServiceToggle(true)
        }
    }

    RunrunScaffold(
        withGradient = false,
        topAppBar = {
            RunrunToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run),
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                },

                )
        },
        floatingActionButton = {
            RunrunFloatingActionButton(
                icon =
                if (state.shouldTrack) {
                    StopIcon
                } else {
                    StartIcon
                },
                iconSize = 20.dp,
                contentDescription = if (state.shouldTrack) {
                    stringResource(id = R.string.pause_run)
                } else {
                    stringResource(id = R.string.start_run)
                }
            ) {
                onAction(ActiveRunAction.OnToggleRunClick)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TrackerMap(
                modifier = Modifier.fillMaxSize(),
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = { bitmap ->
                    val stream = ByteArrayOutputStream()
                    stream.use {
                        bitmap.compress(
                            /* format = */ Bitmap.CompressFormat.JPEG,
                            /* quality = */ 80,
                            /* stream = */ it
                        )
                    }
                    onAction(ActiveRunAction.OnRunProcessed(mapPictureBytes = stream.toByteArray()))
                },
            )
            RunDataCard(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(padding)
                    .fillMaxWidth(),
                elapsedTime = state.elapsedTime,
                runData = state.runData
            )
        }
    }
    if (!state.shouldTrack && state.hasStartedRunning) {
        RunrunDialog(
            title = stringResource(id = R.string.running_is_paused),
            onDismiss = {
                onAction(ActiveRunAction.OnResumeRunClick)
            },
            description = stringResource(id = R.string.resume_or_finish_run),
            primaryButton = {
                RunActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.resume),
                    isLoading = false
                ) {
                    onAction(ActiveRunAction.OnResumeRunClick)
                }
            },
            secondaryButton = {
                RunOutlinedActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.finish),
                    isLoading = state.isSavingRun
                ) {
                    onAction(ActiveRunAction.OnFinishRunClick)
                }
            }
        )
    }
    if (state.showLocationRationale || state.showNotificationRationale) {
        RunrunDialog(
            title = stringResource(id = R.string.permission_required),
            onDismiss = { /* Dismiss not allowed here */ },
            description = when {
                state.showLocationRationale && state.showNotificationRationale -> {
                    stringResource(id = R.string.location_notification_rationale)
                }

                state.showLocationRationale -> {
                    stringResource(id = R.string.location_rationale)
                }

                else -> stringResource(id = R.string.notification_rationale)
            },
            primaryButton = {
                RunOutlinedActionButton(
                    text = stringResource(id = R.string.okay),
                    isLoading = false,
                ) {
                    onAction(ActiveRunAction.DismissRationaleDialog)
                    permissionLauncher.requestRunrunPermissions(context = context)
                }
            })
    }
}

private fun ActivityResultLauncher<Array<String>>.requestRunrunPermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    val notificationPermission = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else arrayOf()

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermission)
        }

        !hasLocationPermission -> {
            launch(locationPermissions)
        }

        !hasNotificationPermission -> {
            launch(notificationPermission)
        }
    }

}

@Composable
@Preview
private fun ActiveRunScreenPreview() {
    RunTheme {
        ActiveRunScreen(
            onServiceToggle = { },
            state = ActiveRunState()
        ) {

        }
    }
}
