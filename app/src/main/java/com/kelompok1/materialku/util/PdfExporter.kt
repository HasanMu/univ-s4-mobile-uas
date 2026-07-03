package com.kelompok1.materialku.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.kelompok1.materialku.domain.repository.LaporanData
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfExporter @Inject constructor() {

    fun export(context: Context, data: LaporanData): android.net.Uri {
        // A4 approx @ 72dpi: 595 × 842 pt.
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = doc.startPage(pageInfo)
        var canvas = page.canvas

        val titlePaint = Paint().apply { textSize = 20f; isFakeBoldText = true }
        val sectionPaint = Paint().apply { textSize = 13f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 11f }
        val labelPaint = Paint().apply { textSize = 9f; color = 0xFF718096.toInt() }

        val leftMargin = 40f
        val rightMargin = 40f
        val pageWidth = 595f
        val pageHeight = 842f
        val bottomLimit = pageHeight - 40f

        var y = 60f

        // Header
        canvas.drawText("Laporan MaterialKu", leftMargin, y, titlePaint)
        y += 22f
        canvas.drawText("Periode: ${data.periode.label}", leftMargin, y, bodyPaint)
        y += 14f
        canvas.drawText(
            "${data.from} s/d ${data.to}",
            leftMargin, y, labelPaint
        )
        y += 24f

        // Divider
        canvas.drawLine(leftMargin, y, pageWidth - rightMargin, y, labelPaint)
        y += 20f

        // Ringkasan
        canvas.drawText("Ringkasan", leftMargin, y, sectionPaint)
        y += 16f
        canvas.drawText("Total Transaksi   : ${data.totalTransaksi}", leftMargin, y, bodyPaint); y += 14f
        canvas.drawText("Pendapatan        : ${Formatter.rupiah(data.pendapatan)}", leftMargin, y, bodyPaint); y += 14f
        canvas.drawText("Material Terjual  : ${data.unitTerjual} unit", leftMargin, y, bodyPaint); y += 24f

        // Transaksi
        canvas.drawText("Transaksi Selesai", leftMargin, y, sectionPaint)
        y += 16f
        if (data.transaksiTerbaru.isEmpty()) {
            canvas.drawText("(tidak ada)", leftMargin, y, bodyPaint); y += 14f
        } else {
            val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            for (t in data.transaksiTerbaru) {
                if (y > bottomLimit - 20) {
                    doc.finishPage(page)
                    page = doc.startPage(pageInfo)
                    canvas = page.canvas
                    y = 60f
                }
                canvas.drawText(
                    "${t.noFaktur}   ${t.tanggal.format(fmt)}   ${Formatter.rupiah(t.totalHarga)}",
                    leftMargin, y, bodyPaint
                )
                y += 14f
            }
        }
        y += 12f

        // Stok Kritis
        if (y > bottomLimit - 60) {
            doc.finishPage(page)
            page = doc.startPage(pageInfo)
            canvas = page.canvas
            y = 60f
        }
        canvas.drawText("Stok Kritis", leftMargin, y, sectionPaint)
        y += 16f
        if (data.stokKritis.isEmpty()) {
            canvas.drawText("(tidak ada)", leftMargin, y, bodyPaint); y += 14f
        } else {
            for (m in data.stokKritis) {
                if (y > bottomLimit - 20) {
                    doc.finishPage(page)
                    page = doc.startPage(pageInfo)
                    canvas = page.canvas
                    y = 60f
                }
                canvas.drawText(
                    "${m.nama}   stok: ${m.stokSaat} (min: ${m.stokMin})",
                    leftMargin, y, bodyPaint
                )
                y += 14f
            }
        }

        // Footer
        y = bottomLimit + 20f
        canvas.drawText(
            "Dicetak: ${java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}",
            leftMargin, y, labelPaint
        )

        doc.finishPage(page)

        val dir = File(context.cacheDir, "reports").apply { mkdirs() }
        val fileName = "laporan-materialku-${System.currentTimeMillis()}.pdf"
        val file = File(dir, fileName)
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
