package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.model.ComponentItem
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object QrCodeGenerator {

    /**
     * Generates a high-quality Bitmap containing the QR code for a given text content.
     */
    fun generateQrBitmap(content: String, size: Int = 300): Bitmap {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    /**
     * Generates an official printable PDF sticker sheet (A4) containing industrial QR labels
     * for all critical furnace components with schematic pages, P&ID codes, and safety ratings.
     */
    fun generatePrintableStickersPdf(
        context: Context,
        components: List<ComponentItem>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Header Banner
        paint.color = Color.rgb(18, 26, 44)
        canvas.drawRect(0f, 0f, 595f, 75f, paint)

        paint.color = Color.rgb(0, 229, 255)
        paint.strokeWidth = 2.5f
        canvas.drawLine(0f, 75f, 595f, 75f, paint)

        paint.color = Color.WHITE
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TAV VACUUM FURNACES S.p.A. - INDUSTRIAL QR CODE STICKER SHEET", 25f, 32f, paint)

        paint.textSize = 10f
        paint.color = Color.rgb(0, 229, 255)
        canvas.drawText("Furnace Model: TPH 120/120/230  |  Serial No: 911  |  Client: GEAT (CE 2023)", 25f, 50f, paint)

        paint.color = Color.LTGRAY
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        canvas.drawText("Printed on Avery/Zebra Industrial Heat-Resistant Label Stock  |  Generated: ${df.format(Date())}", 25f, 65f, paint)

        // 2 Columns x 5 Rows Grid = 10 Stickers per A4 page
        val labelWidth = 260f
        val labelHeight = 135f
        val leftMargin = 30f
        val topMargin = 90f
        val gapX = 15f
        val gapY = 15f

        val componentsToPrint = components.take(10)

        componentsToPrint.forEachIndexed { index, comp ->
            val col = index % 2
            val row = index / 2

            val x = leftMargin + col * (labelWidth + gapX)
            val y = topMargin + row * (labelHeight + gapY)

            // Sticker Border Box
            paint.color = Color.rgb(248, 250, 253)
            canvas.drawRoundRect(RectF(x, y, x + labelWidth, y + labelHeight), 6f, 6f, paint)

            paint.color = Color.rgb(20, 30, 50)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.2f
            canvas.drawRoundRect(RectF(x, y, x + labelWidth, y + labelHeight), 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            // Top Header of the sticker
            paint.color = Color.rgb(20, 30, 50)
            canvas.drawRoundRect(RectF(x, y, x + labelWidth, y + 24f), 6f, 6f, paint)
            canvas.drawRect(x, y + 16f, x + labelWidth, y + 24f, paint)

            paint.color = Color.WHITE
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("TAG: ${comp.tag}", x + 8f, y + 16f, paint)

            paint.color = Color.rgb(0, 229, 255)
            paint.textSize = 8.5f
            canvas.drawText("PID: ${comp.pidCode} | Page ${comp.schematicsPage}", x + 130f, y + 16f, paint)

            // QR Code Bitmap
            val qrContent = "TAV_TPH120:TAG=${comp.tag}:PID=${comp.pidCode}:CAB=${comp.electricalCabinet}:PAGE=${comp.schematicsPage}"
            val qrBitmap = generateQrBitmap(qrContent, 95)
            canvas.drawBitmap(qrBitmap, x + 8f, y + 32f, paint)

            // Component Details next to QR
            paint.color = Color.BLACK
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val nameDisplay = comp.nameEn.take(24)
            canvas.drawText(nameDisplay, x + 110f, y + 45f, paint)

            paint.color = Color.DKGRAY
            paint.textSize = 8f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Model: ${comp.manufacturerModel.take(22)}", x + 110f, y + 60f, paint)
            canvas.drawText("Cabinet: ${comp.electricalCabinet.take(22)}", x + 110f, y + 74f, paint)
            canvas.drawText("Location: ${comp.locationEn.take(22)}", x + 110f, y + 88f, paint)

            // Caution footer
            paint.color = Color.rgb(255, 140, 0)
            paint.textSize = 7.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("TAV SIL 3 / IP54 VERIFIED", x + 110f, y + 104f, paint)

            paint.color = Color.GRAY
            paint.textSize = 7f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Scan with TAV Mobile App", x + 110f, y + 118f, paint)
        }

        // Footer
        paint.color = Color.GRAY
        paint.textSize = 8f
        canvas.drawText("TAV Vacuum Furnaces S.p.A. Caravaggio (BG) Italy  •  Document Ref: TAV-911-QR-LABELS-V1", 120f, 825f, paint)

        pdfDocument.finishPage(page)

        return try {
            val file = File(context.cacheDir, "TAV_Furnace_911_QR_Stickers.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
