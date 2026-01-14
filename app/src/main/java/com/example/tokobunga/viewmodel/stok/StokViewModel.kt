package com.example.tokobunga.viewmodel.stok

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tokobunga.modeldata.LogStok
import com.example.tokobunga.repositori.RepositoryStok
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface StatusStokUi {
    object Idle : StatusStokUi
    object Loading : StatusStokUi
    object Success : StatusStokUi
    data class Error(val message: String) : StatusStokUi
}

class StokViewModel(
    private val repositoryStok: RepositoryStok
) : ViewModel() {

    var jumlah by mutableStateOf("")
        private set

    // PERBAIKAN: Gunakan label yang sama dengan pilihan di PHP ("Masuk" / "Keluar")
    var jenis by mutableStateOf("Masuk")
        private set

    var statusUi: StatusStokUi by mutableStateOf(StatusStokUi.Idle)
        private set

    var logStok by mutableStateOf<List<LogStok>>(emptyList())
        private set

    fun onJumlahChange(value: String) {
        if (value.all { it.isDigit() }) {
            jumlah = value
        }
    }

    fun onJenisChange(value: String) {
        jenis = value
    }

    fun loadLogStok(idBunga: Int) {
        viewModelScope.launch {
            try {
                logStok = repositoryStok.getLogStok(idBunga)
            } catch (e: Exception) {
                logStok = emptyList()
            }
        }
    }

    fun submitStok(idBunga: Int) {
        if (jumlah.isBlank() || jumlah.toInt() <= 0) {
            statusUi = StatusStokUi.Error("Jumlah harus lebih dari 0")
            return
        }

        statusUi = StatusStokUi.Loading

        viewModelScope.launch {
            try {
                /**
                 * PERBAIKAN UTAMA:
                 * Konversi idBunga dan jumlah menjadi Int sesuai kontrak Repository baru.
                 * Pastikan parameter 'tipe' sesuai dengan yang diharapkan PHP.
                 */
                val response = repositoryStok.updateStok(
                    idBunga = idBunga,           // Sudah Int
                    tipe = jenis,                // Kirim "Masuk" atau "Keluar"
                    jumlah = jumlah.toInt()      // Konversi String ke Int
                )

                if (response.isSuccessful) {
                    statusUi = StatusStokUi.Success
                    jumlah = "" // Reset input
                    loadLogStok(idBunga) // Refresh riwayat
                } else {
                    statusUi = StatusStokUi.Error("Gagal: Stok mungkin tidak cukup")
                }

            } catch (e: IOException) {
                statusUi = StatusStokUi.Error("Masalah koneksi internet")
            } catch (e: Exception) {
                statusUi = StatusStokUi.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun resetStatus() {
        statusUi = StatusStokUi.Idle
    }
}