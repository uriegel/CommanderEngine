package de.uriegel.commanderengine.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.*
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
                            Text("Commander Engine")
                        })
                    }, content = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (permissionState)
                                MainScreen({startService()}, {stopService()}, it)
                            else
                                Test1()
                        } else {
                            when {
                                storagePermissionState.status.isGranted ->
                                    MainScreen({startService()}, {stopService()}, it)
                                storagePermissionState.status.shouldShowRationale -> Test2()
                                !storagePermissionState.status.isGranted
                                        && !storagePermissionState.status.shouldShowRationale ->
                                    Test1()
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

@Composable
fun Test1() {
    Text("Test 1")
}

@Composable
fun Test2() {
    Text("Test 2")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    CommanderEngineTheme {
        MainScreen({}, {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServiceAlertPreview() {
    CommanderEngineTheme {
        ServiceAlertDialog({}, {})
    }
}