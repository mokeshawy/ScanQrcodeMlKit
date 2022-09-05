package com.example.scanqrcodemlkit.core.scan_qrcode_manager

interface QrcodeResultHandler {
    fun onReadQrcodeSuccess(qrcodeResult : String)
}