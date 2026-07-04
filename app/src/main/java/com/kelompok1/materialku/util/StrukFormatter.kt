package com.kelompok1.materialku.util

import com.kelompok1.materialku.domain.repository.TrxDetail
import java.time.format.DateTimeFormatter

/**
 * Bikin representasi teks struk ala thermal printer 58mm (32 karakter/baris).
 * Output plain-text bisa langsung ditampilkan di preview atau dikirim byte-by-byte
 * ke `BluetoothSocket.outputStream` kalau printer BT-SPP sudah tersedia (SPP UUID
 * 00001101-0000-1000-8000-00805F9B34FB). ESC/POS control codes (bold, cut, dsb)
 * belum dipakai — kalau nanti pakai printer nyata tinggal wrap section-nya
 * dengan byte prefix ESC/POS.
 */
object StrukFormatter {

    private const val WIDTH = 32
    private val DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    fun format(
        detail: TrxDetail,
        namaToko: String = "MATERIALKU",
        alamatToko: String = "Toko Bahan Bangunan"
    ): String = buildString {
        appendLine(center(namaToko))
        appendLine(center(alamatToko))
        appendLine(divider('='))

        val t = detail.transaksi
        appendLine("No   : ${t.noFaktur}")
        appendLine("Tgl  : ${t.tanggal.format(DATETIME_FMT)}")
        appendLine("Kasir: ${detail.kasir}")
        appendLine(divider('-'))

        for (item in detail.items) {
            appendLine(item.namaMaterial.take(WIDTH))
            val qtyHarga = "  ${item.qty} x ${trimRupiah(item.hargaSatuan)}"
            val sub = trimRupiah(item.subtotal)
            appendLine(spread(qtyHarga, sub))
        }
        appendLine(divider('-'))

        appendLine(spread("Total", trimRupiah(t.totalHarga)))
        appendLine("Status: ${t.status.name}")
        appendLine(divider('='))
        appendLine(center("Terima kasih!"))
        appendLine(center("~ MaterialKu ~"))
    }

    private fun center(text: String): String {
        val t = text.take(WIDTH)
        val pad = (WIDTH - t.length) / 2
        return " ".repeat(pad) + t
    }

    private fun spread(left: String, right: String): String {
        val l = left.take(WIDTH)
        val r = right.take(WIDTH - l.length - 1)
        val gap = WIDTH - l.length - r.length
        return l + " ".repeat(gap.coerceAtLeast(1)) + r
    }

    private fun divider(char: Char): String = char.toString().repeat(WIDTH)

    private fun trimRupiah(amount: Double): String =
        Formatter.rupiah(amount).removePrefix("Rp ").let { "Rp$it" }
}
