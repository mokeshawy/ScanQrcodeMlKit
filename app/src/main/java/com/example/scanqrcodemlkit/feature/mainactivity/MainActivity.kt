package com.example.scanqrcodemlkit.feature.mainactivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.scanqrcodemlkit.R
import com.example.scanqrcodemlkit.databinding.ActivityMainBinding
import com.example.scanqrcodemlkit.feature.scanQrcodeActivity.ScanQrcodeActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.startScanBtn.setOnClickListener {
            startScanQrcodeActivity()
        }
    }

    private fun startScanQrcodeActivity() {
        val intent = Intent(this, ScanQrcodeActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val result = it.data?.getStringExtra("QRCODE")
                binding.resultTv.text = result
            }
        }
}