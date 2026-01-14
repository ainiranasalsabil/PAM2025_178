package com.example.tokobunga.viewmodel.bunga

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tokobunga.modeldata.Bunga
import com.example.tokobunga.repositori.RepositoryBunga
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditBungaViewModel(
    private val repositoryBunga: RepositoryBunga
) : ViewModel() {

    var idBunga: Int = 0
        private set

    var nama by mutableStateOf("")
        private set

    var kategori by mutableStateOf("")
        private set

    var harga by mutableStateOf("")
        private set

    var stok by mutableStateOf("")
        private set

    private var fotoFile: File? = null

    fun loadDataById(id: Int) {
        viewModelScope.launch {
            try {
                val bunga = repositoryBunga.getDetailBunga(id)
                loadBunga(bunga)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadBunga(bunga: Bunga) {
        idBunga = bunga.id_bunga
        nama = bunga.nama_bunga
        kategori = bunga.kategori
        harga = bunga.harga
        stok = bunga.stok.toString()
    }

    fun onNamaChange(value: String) { nama = value }
    fun onKategoriChange(value: String) { kategori = value }
    fun onHargaChange(value: String) { harga = value }
    fun onStokChange(value: String) { stok = value }
    fun onFotoSelected(file: File) { fotoFile = file }

    fun updateBunga(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nama.isBlank() || kategori.isBlank() || harga.isBlank() || stok.isBlank()) {
            onError("Semua data wajib diisi!")
            return
        }

        viewModelScope.launch {
            try {
                /**
                 * PERBAIKAN:
                 * Hapus semua kode .toRequestBody().
                 * Langsung kirim variabel String ke Repository.
                 */
                val fotoPart = fotoFile?.let {
                    MultipartBody.Part.createFormData(
                        "foto",
                        it.name,
                        it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                // Panggil repository dengan parameter String murni
                repositoryBunga.updateBunga(
                    id = idBunga,
                    nama = nama,
                    kategori = kategori,
                    harga = harga,
                    stok = stok,
                    foto = fotoPart
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal memperbarui data bunga")
            }
        }
    }
}