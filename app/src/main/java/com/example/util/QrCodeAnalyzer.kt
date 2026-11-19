package com.example.util

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer

/**
 * High-reliability QR and Barcode Analyzer for CameraX.
 * Uses CameraX toBitmap() to prevent planar YUV rowStride alignment corruption,
 * handles sensor rotation, and evaluates multiple binarization passes (Hybrid, GlobalHistogram, and Inverted)
 * to reliably detect QR codes on physical paper, phone screens, and engraved metal nameplates.
 */
class QrCodeAnalyzer(
    private val onCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        val hints = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to listOf(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.CODE_128,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.EAN_13,
                BarcodeFormat.UPC_A,
                BarcodeFormat.CODE_39,
                BarcodeFormat.ITF
            ),
            DecodeHintType.TRY_HARDER to true,
            DecodeHintType.CHARACTER_SET to "UTF-8"
        )
        setHints(hints)
    }

    private var lastScannedTimestamp = 0L

    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        // Rate-limit detections after a successful scan to prevent UI thrashing
        if (currentTimestamp - lastScannedTimestamp < 750L) {
            imageProxy.close()
            return
        }

        try {
            // CameraX 1.3+ toBitmap() accurately converts YUV_420_888 taking into account rowStride and pixelStride
            val rawBitmap = imageProxy.toBitmap()
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees

            val bitmap = if (rotationDegrees != 0) {
                val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
            } else {
                rawBitmap
            }

            val resultText = decodeBitmapInternal(reader, bitmap)
            if (!resultText.isNullOrBlank()) {
                lastScannedTimestamp = currentTimestamp
                onCodeScanned(resultText)
            }
        } catch (_: Exception) {
            // Unreadable frame, perfectly normal during camera movement
        } finally {
            imageProxy.close()
        }
    }

    companion object {
        /**
         * Decodes a QR code or barcode from any static Bitmap (e.g. from gallery photo or screenshot).
         */
        fun decodeBitmap(bitmap: Bitmap): String? {
            val reader = MultiFormatReader().apply {
                val hints = mapOf(
                    DecodeHintType.POSSIBLE_FORMATS to listOf(
                        BarcodeFormat.QR_CODE,
                        BarcodeFormat.CODE_128,
                        BarcodeFormat.DATA_MATRIX,
                        BarcodeFormat.EAN_13,
                        BarcodeFormat.UPC_A,
                        BarcodeFormat.CODE_39
                    ),
                    DecodeHintType.TRY_HARDER to true,
                    DecodeHintType.CHARACTER_SET to "UTF-8"
                )
                setHints(hints)
            }
            return decodeBitmapInternal(reader, bitmap)
        }

        private fun decodeBitmapInternal(reader: MultiFormatReader, bitmap: Bitmap): String? {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)

            // Pass 1: Standard Hybrid Binarizer
            try {
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                val result = reader.decodeWithState(binaryBitmap)
                if (result != null && result.text.isNotBlank()) return result.text
            } catch (_: Exception) {
            } finally {
                reader.reset()
            }

            // Pass 2: Global Histogram Binarizer (handles uneven glare and shadows)
            try {
                val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
                val result = reader.decodeWithState(binaryBitmap)
                if (result != null && result.text.isNotBlank()) return result.text
            } catch (_: Exception) {
            } finally {
                reader.reset()
            }

            // Pass 3: Inverted Source (for white-on-dark QR codes or etched stainless steel labels)
            try {
                val invertedSource = source.invert()
                val binaryBitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                val result = reader.decodeWithState(binaryBitmap)
                if (result != null && result.text.isNotBlank()) return result.text
            } catch (_: Exception) {
            } finally {
                reader.reset()
            }

            // Pass 4: Center Reticle Crop (70% focus area for distant labels)
            if (width > 240 && height > 240) {
                try {
                    val cropW = (width * 0.72).toInt()
                    val cropH = (height * 0.72).toInt()
                    val cropX = (width - cropW) / 2
                    val cropY = (height - cropH) / 2
                    val cropped = source.crop(cropX, cropY, cropW, cropH)
                    val binaryBitmap = BinaryBitmap(HybridBinarizer(cropped))
                    val result = reader.decodeWithState(binaryBitmap)
                    if (result != null && result.text.isNotBlank()) return result.text
                } catch (_: Exception) {
                } finally {
                    reader.reset()
                }
            }

            return null
        }
    }
}
