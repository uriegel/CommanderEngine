package de.uriegel.commanderengine.ui

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import de.uriegel.commanderengine.BuildConfig
import androidx.core.net.toUri

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermissions(storagePermissionState: PermissionState, setPermissionState: (Boolean)->Unit,
                     hasAllFilesPermission: ()->Boolean) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                            && !hasAllFilesPermission()) {
                            val uri = "package:${BuildConfig.APPLICATION_ID}".toUri()
                            val intent = Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                uri
                            )
                            context.startActivity(intent)
                        }
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                            storagePermissionState.launchPermissionRequest()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                            setPermissionState(hasAllFilesPermission())
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