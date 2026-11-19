package com.example.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LotoPermitData(
    val permitNumber: String,
    val technicianName: String,
    val supervisorName: String,
    val workDescription: String,
    val padlockNumber: String,
    val isElectricalIsolated: Boolean,
    val isPneumaticIsolated: Boolean,
    val isGasIsolated: Boolean,
    val isZeroEnergyVerified: Boolean,
    val isChamberAtmosphereVerified: Boolean,
    val estimatedDurationHours: Double
)

object LotoPermitGenerator {

    fun generateLotoPermitPdf(
        context: Context,
        data: LotoPermitData
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Red/Navy Danger Banner Header
        paint.color = Color.rgb(180, 20, 20) // Industrial Safety Red
        canvas.drawRect(0f, 0f, 595f, 85f, paint)

        paint.color = Color.rgb(255, 230, 0)
        paint.strokeWidth = 3f
        canvas.drawLine(0f, 85f, 595f, 85f, paint)

        paint.color = Color.WHITE
        paint.textSize = 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DANGER - LOCKOUT / TAGOUT & SAFE ENTRY PERMIT", 30f, 32f, paint)

        paint.textSize = 10f
        paint.color = Color.rgb(255, 230, 0)
        canvas.drawText("TAV VACUUM FURNACES S.p.A. - TPH 120 (S/N 911) | GEAT CLIENT FACILITY", 30f, 50f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        canvas.drawText("Permit No: ${data.permitNumber}  |  Issued: ${df.format(Date())}  |  CE SIL 3 Compliant", 30f, 68f, paint)

        var y = 110f

        // Box 1: General Information
        paint.color = Color.rgb(245, 247, 252)
        canvas.drawRoundRect(RectF(30f, y, 565f, y + 80f), 8f, 8f, paint)

        paint.color = Color.BLACK
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. AUTHORIZATION & EQUIPMENT IDENTIFICATION", 45f, y + 22f, paint)

        paint.color = Color.DKGRAY
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Equipment: TAV Horizontal Vacuum Furnace TPH 120/120/230 (480 kW, 750 kVA, 6 bar)", 45f, y + 40f, paint)
        canvas.drawText("Authorized Technician: ${data.technicianName.ifBlank { "Certified LOTO Officer" }}  |  Padlock ID: ${data.padlockNumber}", 45f, y + 55f, paint)
        canvas.drawText("Reason for Lockout: ${data.workDescription.ifBlank { "Preventive hot zone & vacuum pump mechanical service" }}", 45f, y + 70f, paint)

        y += 95f

        // Box 2: Verification Checklist (5 Steps)
        paint.color = Color.rgb(255, 250, 240)
        canvas.drawRoundRect(RectF(30f, y, 565f, y + 175f), 8f, 8f, paint)

        paint.color = Color.rgb(180, 20, 20)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. MANDATORY ENERGY ISOLATION VERIFICATION STEPS", 45f, y + 22f, paint)

        val steps = listOf(
            Pair("Electrical Isolation (QS1 1250A Disconnector): Turned OFF, locked, and tagged", data.isElectricalIsolated),
            Pair("Pneumatic Compressed Air (6.0 bar supply line): Valve shut, line depressurized & locked", data.isPneumaticIsolated),
            Pair("Quench Gas Line (N2/Ar cylinders 6-8 bar): High pressure valves shut & vented", data.isGasIsolated),
            Pair("Zero Energy State (Multimeter & Gauge verification): Voltage < 5V, line pressure = 0 bar", data.isZeroEnergyVerified),
            Pair("Chamber Atmosphere (Confined Space): O2 > 19.5%, temperature < 40°C, vented to air", data.isChamberAtmosphereVerified)
        )

        var stepY = y + 42f
        paint.textSize = 9.5f
        steps.forEachIndexed { idx, step ->
            val statusText = if (step.second) "[✓] PASS - VERIFIED" else "[X] PENDING"
            paint.color = if (step.second) Color.rgb(0, 140, 60) else Color.rgb(200, 0, 0)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(statusText, 45f, stepY, paint)

            paint.color = Color.BLACK
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Step ${idx + 1}: ${step.first}", 165f, stepY, paint)
            stepY += 24f
        }

        y += 190f

        // Box 3: Precautions & Safety Interlocks
        paint.color = Color.rgb(240, 244, 250)
        canvas.drawRoundRect(RectF(30f, y, 565f, y + 80f), 8f, 8f, paint)

        paint.color = Color.rgb(20, 30, 60)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. SAFETY INTERLOCKS & SPECIAL PRECAUTIONS", 45f, y + 22f, paint)

        paint.color = Color.DKGRAY
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("• Pilz PSS 4000 SIL 3 safety loop will block automatic start until physical lock is removed.", 45f, y + 40f, paint)
        canvas.drawText("• Door two-hand control (ACT1+ACT2) disabled while door safety latch mechanical pin is engaged.", 45f, y + 54f, paint)
        canvas.drawText("• Do not enter chamber without secondary safety observer stationed outside the autoclave opening.", 45f, y + 68f, paint)

        y += 105f

        // Signatures Section
        paint.color = Color.rgb(30, 45, 75)
        paint.strokeWidth = 1f
        canvas.drawLine(45f, y + 55f, 245f, y + 55f, paint)
        canvas.drawLine(345f, y + 55f, 545f, y + 55f, paint)

        paint.textSize = 10f
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Certified LOTO Technician", 45f, y + 70f, paint)
        canvas.drawText("Plant Safety Supervisor (GEAT)", 345f, y + 70f, paint)

        paint.color = Color.GRAY
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f
        canvas.drawText("Signature & Date: ${df.format(Date())}", 45f, y + 85f, paint)
        canvas.drawText("Approval Signature: ${data.supervisorName.ifBlank { "Safety Engineer" }}", 345f, y + 85f, paint)

        // Footer
        paint.color = Color.LTGRAY
        paint.textSize = 8f
        canvas.drawText("TAV Vacuum Furnaces S.p.A. Caravaggio (BG) Italy  •  Doc Ref: TAV-911-LOTO-PERMIT-V1", 130f, 825f, paint)

        pdfDocument.finishPage(page)

        return try {
            val file = File(context.cacheDir, "TAV_911_LOTO_Permit_${data.permitNumber}.pdf")
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
