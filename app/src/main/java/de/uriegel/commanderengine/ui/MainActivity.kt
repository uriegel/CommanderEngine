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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.*
import de.uriegel.commanderengine.R
import de.uriegel.commanderengine.extensions.isPermanentlyDenied
import de.uriegel.commanderengine.extensions.startService
import de.uriegel.commanderengine.extensions.stopService
import de.uriegel.commanderengine.ui.theme.CommanderEngineTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(topBar = {
                        TopAppBar(title = {
                            Text(getString(R.string.app_title) )
                        })
                    }, content = {

                        val permissions = getPermissions()
                        PermissionCheck(stringResource(R.string.app_title), permissions.map { it.permission }.toList().toTypedArray(),
                            permissions.map { it.rationale }.toList().toTypedArray()) {
                        }
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

    fun getPermissions() = sequence {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            yield(Permission(Manifest.permission.READ_EXTERNAL_STORAGE,
                //R.string.permission_external_storage_rationale))
                R.string.app_title))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            yield(Permission(Manifest.permission.POST_NOTIFICATIONS,
                R.string.app_title))
                //R.string.permission_notification_rationale))
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasAllFilesPermission() =
        Environment.isExternalStorageManager()
}
