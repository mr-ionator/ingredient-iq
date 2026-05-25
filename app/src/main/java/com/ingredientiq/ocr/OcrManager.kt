package com.ingredientiq.ocr

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

sealed class OcrState {
    object Idle : OcrState()
    object Scanning : OcrState()
    data class TextDetected(val rawText: String) : OcrState()
    data class Error(val message: String) : OcrState()
}

@Singleton
class OcrManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val _state = MutableStateFlow<OcrState>(OcrState.Idle)
    val state: StateFlow<OcrState> = _state

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val scope = CoroutineScope(Dispatchers.Main)

    private var lastText = ""
    private var debounceJob: Job? = null

    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor, ::analyzeImage) }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis,
                )
                _state.value = OcrState.Scanning
            } catch (e: Exception) {
                _state.value = OcrState.Error(e.message ?: "Camera bind failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun analyzeImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val newText = visionText.text
                if (newText.isNotBlank() && levenshteinGate(lastText, newText) > 10) {
                    debounceJob?.cancel()
                    debounceJob = scope.launch {
                        delay(300)
                        lastText = newText
                        _state.value = OcrState.TextDetected(newText)
                    }
                }
            }
            .addOnFailureListener { e ->
                _state.value = OcrState.Error(e.message ?: "OCR failed")
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    fun reset() {
        lastText = ""
        _state.value = OcrState.Scanning
    }

    fun shutdown() {
        cameraExecutor.shutdown()
        recognizer.close()
    }

    private fun levenshteinGate(a: String, b: String): Int {
        if (a == b) return 0
        val maxLen = maxOf(a.length, b.length)
        if (maxLen == 0) return 0
        // Fast approximation: compare lengths and prefix/suffix differences
        return Math.abs(a.length - b.length) +
            a.zip(b).count { (ca, cb) -> ca != cb }
    }
}
