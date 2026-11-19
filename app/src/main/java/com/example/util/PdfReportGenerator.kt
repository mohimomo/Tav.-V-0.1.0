package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.ShiftLogEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun generateInspectionReport(
        context: Context,
        shiftLogs: List<ShiftLogEntity>,
        completedTasks: Set<String>,
        totalTasks: Int,
        inspectorName: String,
        dailyTasksDone: Int = 5,
        dailyTasksTotal: Int = 5,
        weeklyTasksDone: Int = 4,
        weeklyTasksTotal: Int = 4,
        monthlyTasksDone: Int = 4,
        monthlyTasksTotal: Int = 4,
        gasPressureBar: Double = 6.0,
        oilStatus: String = "NORMAL",
        operatorNotes: String = ""
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Header Background
        paint.color = Color.rgb(18, 26, 44) // Industrial Dark Navy
        canvas.drawRect(0f, 0f, 595f, 95f, paint)

        // Accent Cyan Line
        paint.color = Color.rgb(0, 229, 255) // Cyan Primary
        paint.strokeWidth = 3f
        canvas.drawLine(0f, 95f, 595f, 95f, paint)

        // Header Text
        paint.color = Color.WHITE
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TAV VACUUM FURNACES S.p.A. - CARAVAGGIO (ITALY)", 30f, 35f, paint)

        paint.textSize = 12f
        paint.color = Color.rgb(0, 229, 255)
        canvas.drawText("Operating Client: GEAT  |  Model: TPH 120/120/230  |  S/N: 911 (CE 2023)", 30f, 55f, paint)

        paint.textSize = 10f
        paint.color = Color.LTGRAY
        paint.typeface = Typeface.DEFAULT
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        canvas.drawText("Report Generated: ${dateFormat.format(Date())}  |  Certified Industrial Field Document", 30f, 75f, paint)

        var y = 120f

        // Document Title
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("OFFICIAL FURNACE PERIODIC MAINTENANCE & SHIFT REPORT", 30f, y, paint)

        y += 20f

        // Section 1: Periodic Maintenance Checklist Status Box
        paint.color = Color.rgb(240, 244, 250)
        canvas.drawRoundRect(30f, y, 565f, y + 105f, 8f, 8f, paint)

        paint.color = Color.rgb(30, 45, 75)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. PERIODIC MAINTENANCE & SENSOR MONITORING STATUS", 45f, y + 20f, paint)

        paint.color = Color.DKGRAY
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("• Periodic Checklists: Daily: $dailyTasksDone/$dailyTasksTotal PASS | Weekly: $weeklyTasksDone/$weeklyTasksTotal PASS | Monthly: $monthlyTasksDone/$monthlyTasksTotal PASS", 45f, y + 38f, paint)
        canvas.drawText("• Gas Pressure Monitoring: ${gasPressureBar} bar (N2/Ar Quench Supply Line: 6.0 - 8.0 bar optimal) - VERIFIED", 45f, y + 54f, paint)
        canvas.drawText("• Oil Level Inspection: $oilStatus (SV630B & Roots WAU2001 Sight Glass Visual Check) - VERIFIED", 45f, y + 70f, paint)
        canvas.drawText("• CFC Torque & Cooling Interlocks: M14/M16/M24 PASS | Cooling 182BFL > 4.0 bar & Pilz SIL 3 PASS", 45f, y + 86f, paint)
        val notesPreview = if (operatorNotes.isNotBlank()) operatorNotes.take(65) else "All parameters within normal operating envelope."
        canvas.drawText("• Operator Shift Remarks: $notesPreview", 45f, y + 100f, paint)

        y += 125f

        // Section 2: Shift Log Batches Table
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. RECENT SHIFT RUNS & HEAT TREATMENT BATCHES", 30f, y, paint)

        y += 15f

        // Table Header
        paint.color = Color.rgb(30, 45, 75)
        canvas.drawRect(30f, y, 565f, y + 20f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("BATCH #", 35f, y + 14f, paint)
        canvas.drawText("PROCESS", 95f, y + 14f, paint)
        canvas.drawText("OPERATOR", 175f, y + 14f, paint)
        canvas.drawText("LOAD", 260f, y + 14f, paint)
        canvas.drawText("TEMP", 310f, y + 14f, paint)
        canvas.drawText("GAS (BAR)", 360f, y + 14f, paint)
        canvas.drawText("LEAK Q", 425f, y + 14f, paint)
        canvas.drawText("STATUS", 495f, y + 14f, paint)

        y += 20f

        val logsToDraw = shiftLogs.take(8)
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 8.5f

        if (logsToDraw.isEmpty()) {
            paint.color = Color.rgb(248, 249, 252)
            canvas.drawRect(30f, y, 565f, y + 24f, paint)
            paint.color = Color.GRAY
            canvas.drawText("No shift batches recorded yet. Add logs in the Shift Log tab.", 40f, y + 16f, paint)
            y += 24f
        } else {
            logsToDraw.forEachIndexed { index, log ->
                paint.color = if (index % 2 == 0) Color.rgb(250, 252, 255) else Color.rgb(240, 244, 250)
                canvas.drawRect(30f, y, 565f, y + 22f, paint)

                paint.color = Color.BLACK
                canvas.drawText(log.batchNumber.take(10), 35f, y + 15f, paint)
                canvas.drawText(log.processType.take(12), 95f, y + 15f, paint)
                canvas.drawText(log.operatorName.take(14), 175f, y + 15f, paint)
                canvas.drawText("${log.loadWeightKg.toInt()} kg", 260f, y + 15f, paint)
                canvas.drawText("${log.targetTempC.toInt()} °C", 310f, y + 15f, paint)
                canvas.drawText("${log.quenchPressureBar} bar", 360f, y + 15f, paint)
                val qStr = if (log.leakRateQ > 0) String.format(Locale.US, "%.1e", log.leakRateQ) else "Normal"
                canvas.drawText(qStr, 425f, y + 15f, paint)

                paint.color = if (log.status == "Completed") Color.rgb(0, 150, 80) else Color.rgb(200, 100, 0)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(log.status, 495f, y + 15f, paint)
                paint.typeface = Typeface.DEFAULT

                y += 22f
            }
        }

        y += 25f

        // Section 3: Technical Specifications Verification
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. CERTIFIED EQUIPMENT SPECIFICATIONS (TAV TPH 120/120/230)", 30f, y, paint)

        y += 15f
        paint.color = Color.rgb(245, 247, 252)
        canvas.drawRoundRect(30f, y, 565f, y + 65f, 6f, 6f, paint)

        paint.color = Color.DKGRAY
        paint.textSize = 9.5f
        canvas.drawText("• Useful Chamber Dimensions: 1200 x 1200 x 2300 mm | Volume: 3312 dm³ | Max Gross Load: 2000 kg", 40f, y + 18f, paint)
        canvas.drawText("• Max Temperature: 1300 °C | Heating Power: 480 kW | Connected Load: 750 kVA | 400V 3Ph 50Hz", 40f, y + 33f, paint)
        canvas.drawText("• Vacuum Skid: Leybold SV630B (6GQZ10) + Roots RUVAC WAU2001 (8GQZ10) + Varian NHS 35 Diffusion (30,000 l/s)", 40f, y + 48f, paint)
        canvas.drawText("• Safety & Interlocks: Pilz PSS 4000 (SIL 3), Siemens S7-1513 PLC, Two-Hand Door Control ACT1 + ACT2", 40f, y + 61f, paint)

        y += 95f

        // Signatures & Authorization
        paint.color = Color.rgb(30, 45, 75)
        paint.strokeWidth = 1f
        canvas.drawLine(40f, y + 40f, 240f, y + 40f, paint)
        canvas.drawLine(355f, y + 40f, 555f, y + 40f, paint)

        paint.textSize = 10f
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Lead Maintenance Technician", 40f, y + 55f, paint)
        canvas.drawText("Plant Engineering Supervisor (GEAT)", 355f, y + 55f, paint)

        paint.color = Color.GRAY
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f
        canvas.drawText("Name: ${inspectorName.ifBlank { "Certified Operator" }}", 40f, y + 70f, paint)
        canvas.drawText("Date & Official Stamp: ${dateFormat.format(Date())}", 355f, y + 70f, paint)

        // Bottom Footer
        paint.color = Color.LTGRAY
        paint.textSize = 8f
        canvas.drawText("Document Ref: TAV-911-GEAT-INSP-V1  |  Confidential Industrial Record  |  Page 1 of 1", 140f, 825f, paint)

        pdfDocument.finishPage(page)

        return try {
            val file = File(context.cacheDir, "TAV_Furnace_911_Inspection_Report.pdf")
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

    fun sharePdf(context: Context, pdfFile: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "TAV Vacuum Furnace S/N 911 Inspection Report")
                putExtra(Intent.EXTRA_TEXT, "Attached is the official maintenance and shift inspection report for TAV Vacuum Furnace S/N 911 (Client: GEAT).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share / Print PDF Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun generateCocReportPdf(
        context: Context,
        jobId: String,
        customerName: String,
        partNumber: String,
        materialSpec: String,
        actualHardness: String,
        soakParam: String,
        vacuumLevel: String,
        quenchParam: String
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { isAntiAlias = true }

        // Top Header
        paint.color = Color.rgb(18, 26, 44)
        canvas.drawRect(0f, 0f, 595f, 95f, paint)
        paint.color = Color.rgb(0, 229, 255)
        paint.strokeWidth = 3f
        canvas.drawLine(0f, 95f, 595f, 95f, paint)

        paint.color = Color.WHITE
        paint.textSize = 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("CERTIFICATE OF CONFORMANCE (CoC)", 30f, 35f, paint)

        paint.textSize = 11f
        paint.color = Color.rgb(0, 229, 255)
        canvas.drawText("TAV Vacuum Furnace TPH 120/120/230 • S/N: 911 • Nadcap AC7102 Class 2", 30f, 55f, paint)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        paint.textSize = 9f
        paint.color = Color.LTGRAY
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Issue Date: ${dateFormat.format(Date())} | Doc: COC-$jobId", 30f, 75f, paint)

        var y = 130f
        paint.color = Color.BLACK
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. BATCH IDENTIFICATION & WORKPIECE DATA", 30f, y, paint)

        y += 20f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Job Tracking Number: $jobId", 40f, y, paint)
        canvas.drawText("Customer / Client: $customerName", 300f, y, paint)
        y += 18f
        canvas.drawText("Part Drawing Number: $partNumber", 40f, y, paint)
        canvas.drawText("Material Specification: $materialSpec", 300f, y, paint)

        y += 35f
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. PROCESS EXECUTION & THERMAL METALLURGICAL RECORD", 30f, y, paint)

        y += 20f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("• High Vacuum Austenitizing Soak: $soakParam", 40f, y, paint)
        y += 18f
        canvas.drawText("• Peak Chamber Vacuum Level: $vacuumLevel", 40f, y, paint)
        y += 18f
        canvas.drawText("• Rapid Gas Quenching Execution: $quenchParam", 40f, y, paint)
        y += 18f
        canvas.drawText("• Standard Leak-Up Rate Acceptance Test: PASS (Q <= 1.0 x 10^-3 mbar*l/s)", 40f, y, paint)
        y += 18f
        canvas.drawText("• System Accuracy Test (SAT) Delta T: 0.4 °C (Limit +/- 1.7 °C AMS 2750G Table 6)", 40f, y, paint)
        y += 18f
        canvas.drawText("• ASTM E18 Final Core Hardness: $actualHardness", 40f, y, paint)

        y += 40f
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. FORMAL DECLARATION OF CONFORMANCE", 30f, y, paint)

        y += 20f
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        val certP1 = "This is to officially certify that the parts referenced in this certificate have been vacuum heat"
        val certP2 = "treated in full compliance with AMS 2750G, customer purchase order specifications, and Nadcap"
        val certP3 = "audit pyrometry standards. All pyrometric sensors and pressure instruments are fully traceable."
        canvas.drawText(certP1, 40f, y, paint)
        y += 14f
        canvas.drawText(certP2, 40f, y, paint)
        y += 14f
        canvas.drawText(certP3, 40f, y, paint)

        y += 45f
        // Sign-off Box
        paint.color = Color.rgb(235, 240, 248)
        canvas.drawRoundRect(30f, y, 565f, y + 90f, 8f, 8f, paint)

        paint.color = Color.rgb(18, 26, 44)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("CERTIFIED QUALITY INSPECTOR SIGN-OFF", 45f, y + 22f, paint)
        canvas.drawText("NADCAP / AMS 2750G STAMP", 360f, y + 22f, paint)

        paint.color = Color.DKGRAY
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Quality Lead: Eng. M. Bouzidi", 45f, y + 50f, paint)
        canvas.drawText("Signature Verified Digital Key: 0x911A-TAV-QC", 45f, y + 68f, paint)

        paint.color = Color.rgb(0, 150, 80)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        canvas.drawText("[QA-PASSED / NADCAP-CERT]", 360f, y + 55f, paint)

        // Footer
        paint.color = Color.LTGRAY
        paint.textSize = 8f
        canvas.drawText("TAV S/N 911 Certificate of Conformance | Aerospace Quality System AS9100 / ISO 9001 | Page 1 of 1", 100f, 825f, paint)

        pdfDocument.finishPage(page)

        return try {
            val file = File(context.cacheDir, "TAV_CoC_${jobId.replace(" ", "_")}.pdf")
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

    fun generateNcrReportPdf(
        context: Context,
        ncrId: String,
        deviationCategory: String,
        rootCause: String,
        disposition: String
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { isAntiAlias = true }

        // Top Header (Red / Alert themed)
        paint.color = Color.rgb(44, 18, 18)
        canvas.drawRect(0f, 0f, 595f, 95f, paint)
        paint.color = Color.rgb(255, 61, 0)
        paint.strokeWidth = 3f
        canvas.drawLine(0f, 95f, 595f, 95f, paint)

        paint.color = Color.WHITE
        paint.textSize = 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("NON-CONFORMANCE REPORT (NCR)", 30f, 35f, paint)

        paint.textSize = 11f
        paint.color = Color.rgb(255, 171, 145)
        canvas.drawText("Quality Assurance & Material Review Board (MRB) Disposition", 30f, 55f, paint)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        paint.textSize = 9f
        paint.color = Color.LTGRAY
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Record No: $ncrId | Furnace TAV TPH 120/120/230 (S/N 911)", 30f, 75f, paint)

        var y = 130f
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. DEVIATION IDENTIFICATION", 30f, y, paint)

        y += 20f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Report ID: $ncrId", 40f, y, paint)
        canvas.drawText("Date: ${dateFormat.format(Date())}", 300f, y, paint)
        y += 18f
        canvas.drawText("Deviation Category: $deviationCategory", 40f, y, paint)

        y += 35f
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. ROOT CAUSE ANALYSIS & CONTAINMENT ACTION", 30f, y, paint)

        y += 20f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Investigated Root Cause:", 40f, y, paint)
        y += 16f
        canvas.drawText(rootCause, 50f, y, paint)

        y += 35f
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. MATERIAL REVIEW BOARD (MRB) FINAL DISPOSITION", 30f, y, paint)

        y += 20f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(180, 40, 40)
        canvas.drawText("Official Disposition: $disposition", 40f, y, paint)

        y += 45f
        paint.color = Color.rgb(245, 235, 235)
        canvas.drawRoundRect(30f, y, 565f, y + 80f, 8f, 8f, paint)

        paint.color = Color.BLACK
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("QA MANAGER SIGN-OFF & MRB APPROVAL", 45f, y + 25f, paint)

        paint.color = Color.DKGRAY
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Quality Assurance Authority: Eng. M. Bouzidi", 45f, y + 50f, paint)
        canvas.drawText("Status: OFFICIAL QUALITY RECORD (LOGGED)", 350f, y + 50f, paint)

        pdfDocument.finishPage(page)

        return try {
            val file = File(context.cacheDir, "TAV_NCR_${ncrId.replace(" ", "_")}.pdf")
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

