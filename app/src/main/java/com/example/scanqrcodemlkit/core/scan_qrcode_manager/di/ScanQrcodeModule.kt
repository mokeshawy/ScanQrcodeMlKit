package com.example.scanqrcodemlkit.core.scan_qrcode_manager.di

import android.app.Activity
import com.example.scanqrcodemlkit.core.scan_qrcode_manager.ScanQrcodeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object ScanQrcodeModule {

    @Provides
    fun provideScanQrcodeManager(activity: Activity) = ScanQrcodeManager(activity)
}