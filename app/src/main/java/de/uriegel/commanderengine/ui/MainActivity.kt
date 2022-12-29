package de.uriegel.commanderengine.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.*
import de.uriegel.commanderengine.R
import de.uriegel.commanderengine.extensions.isPermanentlyDenied
import de.uriegel.commanderengine.ui.theme.CommanderEngineTheme
import de.uriegel.commanderengine.extensions.startService
import de.uriegel.commanderengine.extensions.stopService

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CommanderEngineTheme {
                var permissionState by remember {
                    mutableStateOf(false)
                }
                val storagePermissionState = rememberPermissionState(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                CheckPermissions(storagePermissionState,
                    {permissionState = it}, {hasAllFilesPermission()})

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(title = {
                            Text(getString(R.string.app_title) )
                        })
                    }, content = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (permissionState)
                                MainScreen({startService()}, {stopService()}, it)
                            else
                                DialogScreen(R.string.PERMISSION_DENIED, it)
                        } else {
                            when {
                                storagePermissionState.status.isGranted ->
                                    MainScreen({startService()}, {stopService()}, it)
                                storagePermissionState.status.shouldShowRationale ->
                                    DialogScreen(R.string.PERMISSION_SHOW_RATIONALE, it)
                                storagePermissionState.isPermanentlyDenied() ->
                                    DialogScreen(R.string.PERMISSION_DENIED, it)
                            }
                        }
                    })
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasAllFilesPermission() =
        Environment.isExternalStorageManager()
}
