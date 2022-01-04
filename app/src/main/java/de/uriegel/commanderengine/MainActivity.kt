package de.uriegel.commanderengine

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import de.uriegel.activityextensions.ActivityRequest
import de.uriegel.commanderengine.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        val viewModel = ViewModelProvider(this).get(Model::class.java)
        binding.setVariable(BR.model, viewModel)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        launch {
            val backgroundResult = activityRequest.checkAndAccessPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            if (backgroundResult.any { !it.value }) {
                toast(R.string.no_access, Toast.LENGTH_LONG)
                finish()
                return@launch
            }

            if (Build.VERSION.SDK_INT >= 30 && !hasAllFilesPermission()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                activityRequest.launch(Intent( Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
            }
        }

        binding.btnStart.setOnClickListener {
            val startIntent = Intent(this, Service::class.java)
            startService(startIntent)
        }

        binding.btnStop.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setPositiveButton(R.string.ok) { _, _ ->
                    val startIntent = Intent(this@MainActivity, Service::class.java)
                    stopService(startIntent)
                }
                setNegativeButton(R.string.cancel) { _, _ -> }
            }
            val dialog = builder
                .setMessage(getString(R.string.alert_stop_service))
                .setTitle(getString(R.string.alert_title_stop_service))
                .create()
            dialog.show()
        }
    }

    private fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, this.resources.getText(resId), duration).show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasAllFilesPermission() = Environment.isExternalStorageManager()

    override val coroutineContext = Dispatchers.Main
    private lateinit var binding: ActivityMainBinding
    private val activityRequest = ActivityRequest(this)
}