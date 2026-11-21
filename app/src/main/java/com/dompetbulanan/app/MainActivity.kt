
package com.dompetbulanan.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.content.Context

class MainActivity : Activity() {

    private var saldo = 0.0
    private var pemasukan = 0.0
    private var pengeluaran = 0.0
    private lateinit var saldoText: TextView
    private lateinit var pemasukanText: TextView
    private lateinit var pengeluaranText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("dompet", Context.MODE_PRIVATE)
        saldo = prefs.getFloat("saldo", 0f).toDouble()
        pemasukan = prefs.getFloat("masuk", 0f).toDouble()
        pengeluaran = prefs.getFloat("keluar", 0f).toDouble()

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(28, 32, 28, 28)
        layout.setBackgroundColor(Color.rgb(245, 247, 250))

        val judul = TextView(this)
        judul.text = "DOMPET BULANAN"
        judul.textSize = 25f
        judul.setTextColor(Color.rgb(25, 45, 75))
        judul.gravity = Gravity.CENTER
        layout.addView(judul)

        val subjudul = TextView(this)
        subjudul.text = "Kelola keuanganmu dengan mudah"
        subjudul.textSize = 14f
        subjudul.gravity = Gravity.CENTER
        layout.addView(subjudul)

        saldoText = buatTeks("Saldo: Rp 0", 24f)
        pemasukanText = buatTeks("Pemasukan: Rp 0", 17f)
        pengeluaranText = buatTeks("Pengeluaran: Rp 0", 17f)

        layout.addView(saldoText)
        layout.addView(pemasukanText)
        layout.addView(pengeluaranText)

        tombol(layout, "Tambah Pemasukan") {
            tambahTransaksi(true)
        }

        tombol(layout, "Tambah Pengeluaran") {
            tambahTransaksi(false)
        }

        tombol(layout, "Reset Bulanan") {
            saldo = 0.0
            pemasukan = 0.0
            pengeluaran = 0.0
            simpan()
            perbarui()
        }

        setContentView(layout)
        perbarui()
    }

    private fun buatTeks(teks: String, ukuran: Float): TextView {
        return TextView(this).apply {
            text = teks
            textSize = ukuran
            setTextColor(Color.rgb(30, 40, 55))
            setPadding(0, 24, 0, 24)
        }
    }

    private fun tombol(
        layout: LinearLayout,
        teks: String,
        aksi: () -> Unit
    ) {
        val button = Button(this)
        button.text = teks
        layout.addView(button)
        button.setOnClickListener { aksi() }
    }

    private fun tambahTransaksi(isMasuk: Boolean) {
        val input = EditText(this)
        input.inputType = 2
        input.hint = "Masukkan jumlah rupiah"

        AlertDialog.Builder(this)
            .setTitle(if (isMasuk) "Pemasukan" else "Pengeluaran")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val jumlah = input.text.toString().toDoubleOrNull()
                if (jumlah != null && jumlah > 0) {
                    if (isMasuk) {
                        pemasukan += jumlah
                        saldo += jumlah
                    } else {
                        pengeluaran += jumlah
                        saldo -= jumlah
                    }
                    simpan()
                    perbarui()
                } else {
                    Toast.makeText(this, "Jumlah tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun simpan() {
        getSharedPreferences("dompet", Context.MODE_PRIVATE)
            .edit()
            .putFloat("saldo", saldo.toFloat())
            .putFloat("masuk", pemasukan.toFloat())
            .putFloat("keluar", pengeluaran.toFloat())
            .apply()
    }

    private fun perbarui() {
        saldoText.text = "Saldo: Rp ${saldo.toLong()}"
        pemasukanText.text = "Pemasukan: Rp ${pemasukan.toLong()}"
        pengeluaranText.text = "Pengeluaran: Rp ${pengeluaran.toLong()}"
    }
}
