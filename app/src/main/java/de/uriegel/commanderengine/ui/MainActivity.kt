package de.uriegel.commanderengine.ui

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO ?
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            CommanderEngineTheme {
                var permissionState by remember {
                    mutableStateOf(false)
                }
                CheckPermissions({permissionState = it}) { hasAllFilesPermission() }

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
                        //                    when {
//                        permissionState.status.isGranted -> {
//                            MainScreen()
//                        }
//                        permissionState.status.shouldShowRationale -> {
//                            Test1()
//                        }
//                        !permissionState.status.isGranted && !permissionState.status.shouldShowRationale -> {
//                            Test2()
//                        }
//                    }
                        if (permissionState)
                            MainScreen({startService()}, {stopService()}, it)
                        else
                            Test1()

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