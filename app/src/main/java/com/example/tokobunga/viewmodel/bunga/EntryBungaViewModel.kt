package com.example.tokobunga.viewmodel.bunga

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tokobunga.repositori.RepositoryBunga
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EntryBungaViewModel(
    private val repositoryBunga: RepositoryBunga
) : ViewModel() {

    var nama by mutableStateOf("")
        private set
    var kategori by mutableStateOf("")
        private set
    var harga by mutableStateOf("")
        private set
    var stok by mutableStateOf("")
        private set

    fun onNamaChange(value: String) { nama = value }
    fun onKategoriChange(value: String) { kategori = value }
    fun onHargaChange(value: String) { harga = value }
    fun onStokChange(value: String) { stok = value }

    fun submitBunga(
        fileFoto: File,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                /**
                 * PERBAIKAN:
                 * Tidak perlu lagi membuat namaRB, kategoriRB, dll.
                 * Kita langsung membuat Part untuk FOTO saja.
                 */
                val fotoPart = MultipartBody.Part.createFormData(
                    "foto",
                    fileFoto.name,
                    fileFoto.asRequestBody("image/*".toMediaType())
                )

                /**
                 * Panggil repository dengan String murni.
                 * Stok dikirim sebagai String "10", nanti PHP yang melakukan intval().
                 */
                repositoryBunga.tambahBunga(
                    nama = nama,
                    kategori = kategori,
                    harga = harga,
                    stok = stok,
                    foto = fotoPart
                )

                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal simpan data")
            }
        }
    }
}