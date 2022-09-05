package com.example.scanqrcodemlkit.feature.scanQrcodeActivity

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.scanqrcodemlkit.R
import com.example.scanqrcodemlkit.core.scan_qrcode_manager.QrcodeResultHandler
import com.example.scanqrcodemlkit.core.scan_qrcode_manager.ScanQrcodeManager
import com.example.scanqrcodemlkit.databinding.ActivityScanQrcodeBinding
import javax.inject.Inject

class ScanQrcodeActivity : AppCompatActivity() {

    lateinit var binding: ActivityScanQrcodeBinding
    @Inject
    lateinit var scanQrcodeManager: ScanQrcodeManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan_qrcode)

        initScanQrcodeManager()
        handlePermissionOfCamera()
        scanQrcodeManager.setQrcodeResult {
            Toast.makeText(this,"${it}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun initScanQrcodeManager() {
        scanQrcodeManager = ScanQrcodeManager(this)
    }

    private fun handlePermissionOfCamera() {
        permissionCallback.launch(Manifest.permission.CAMERA)
    }

    private val permissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                scanQrcodeManager.startCamera(binding.previewView)
            } else {
                // handle permission denied
            }
        }
}