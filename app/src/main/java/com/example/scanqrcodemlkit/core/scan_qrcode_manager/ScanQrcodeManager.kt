package com.example.scanqrcodemlkit.core.scan_qrcode_manager

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.RectF
import android.media.Image
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.scanqrcodemlkit.core.scan_qrcode_manager.barcode_view.BarcodeBoxView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class ScanQrcodeManager @Inject constructor(private val activity: Activity) {

    private val appCompatActivity = (activity as AppCompatActivity)
    private lateinit var cameraExecutor: ExecutorService
    lateinit var barcodeBoxView: BarcodeBoxView
    private var scaleX = 1f
    private var scaleY = 1f
    private var qrcodeResult: (String) -> Unit = {}

    init {
        registerLifeCycleStateObserver()
    }

    private fun registerLifeCycleStateObserver() {
        appCompatActivity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                initCameraExecutor()
                initBarCodeBoxView()
                setContentView()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                cameraExecutor.shutdown()
            }
        })
    }

    private fun initCameraExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    private fun initBarCodeBoxView() {
        barcodeBoxView = BarcodeBoxView(activity)
    }

    private fun setContentView() {
        appCompatActivity.addContentView(barcodeBoxView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))
    }

    fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(appCompatActivity,
                    cameraSelector,
                    preview(previewView),
                    imageAnalyzer(previewView))
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    private fun preview(previewView: PreviewView) = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    private fun imageAnalyzer(previewView: PreviewView) = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
            it.setAnalyzer(
                cameraExecutor,
                initializeAnalyze(previewView)
            )
        }


    private fun initializeAnalyze(previewView: PreviewView) =
        ImageAnalysis.Analyzer { image ->
            val img = image.image
            if (img != null) {
                setUpOrientation(previewView, img)
                val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)
                setResult(inputImage)
            }
            image.close()
        }

    private fun setUpOrientation(previewView: PreviewView, img: Image) {
        val orientation = activity.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            scaleX = previewView.width / img.width.toFloat()
            scaleY = previewView.height / img.height.toFloat()
        } else {
            scaleX = previewView.width / img.height.toFloat()
            scaleY = previewView.height / img.width.toFloat()
        }
    }

    private fun setResult(inputImage: InputImage) {
        val options = BarcodeScannerOptions.Builder().build()
        val scanner = BarcodeScanning.getClient(options)
        scanner.process(inputImage).addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrcodeResult(it) }
                    barcode.boundingBox?.let { rect ->
                        barcodeBoxView.setRect(adjustBoundingRect(rect))
                    }
                }
            } else {
                barcodeBoxView.setRect(RectF())
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "Please try again later", Toast.LENGTH_SHORT).show()
        }
    }

    fun setQrcodeResult(qrCodeResult: (String) -> Unit) {
        this.qrcodeResult = qrCodeResult
    }

    private fun adjustBoundingRect(rect: Rect) = RectF(
        translateX(rect.left.toFloat()),
        translateY(rect.top.toFloat()),
        translateX(rect.right.toFloat()),
        translateY(rect.bottom.toFloat())
    )

    private fun translateX(x: Float) = x * scaleX
    private fun translateY(y: Float) = y * scaleY
}