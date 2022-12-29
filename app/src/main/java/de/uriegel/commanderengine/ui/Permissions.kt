package de.uriegel.commanderengine.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.uriegel.commanderengine.BuildConfig

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermissions(setPermissionState: (Boolean)->Unit, hasAllFilesPermission: ()->Boolean) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val permissionsState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                            && !hasAllFilesPermission()) {
                            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                            val intent = Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                uri
                            )
                            context.startActivity(intent)
                        }
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                            permissionsState.launchPermissionRequest()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                            setPermissionState(hasAllFilesPermission())
                        else
                            setPermissionState(permissionsState.status.isGranted)
                    }
                    else -> {}
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
}