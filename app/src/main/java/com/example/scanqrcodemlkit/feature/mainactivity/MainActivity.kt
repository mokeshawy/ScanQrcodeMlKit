package com.example.scanqrcodemlkit.feature.mainactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.scanqrcodemlkit.R
import com.example.scanqrcodemlkit.core.scan_qrcode_manager.QrcodeResultHandler
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
        startActivity(intent)
    }
}