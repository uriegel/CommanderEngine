package de.uriegel.commanderengine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import de.uriegel.commanderengine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        val viewModel = ViewModelProvider(this).get(Model::class.java)
        binding.setVariable(BR.model, viewModel)

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

    private lateinit var binding: ActivityMainBinding
}