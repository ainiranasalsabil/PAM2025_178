package com.example.tokobunga.repositori

import com.example.tokobunga.apiservice.ServiceApiStok
import com.example.tokobunga.modeldata.LogStok
import retrofit2.Response

interface RepositoryStok {
    // PERBAIKAN: Gunakan tipe data Int untuk ID dan Jumlah agar sinkron
    suspend fun updateStok(
        idBunga: Int,
        tipe: String, // Sesuaikan nama parameter dengan PHP (tipe)
        jumlah: Int
    ): Response<Void>

    suspend fun getLogStok(idBunga: Int): List<LogStok>
}

class JaringanRepositoryStok(
    private val serviceApiStok: ServiceApiStok
) : RepositoryStok {

    override suspend fun updateStok(
        idBunga: Int,
        tipe: String,
        jumlah: Int
    ): Response<Void> {
        /**
         * PERBAIKAN:
         * Tidak perlu lagi menggunakan mapOf().
         * Langsung kirim parameter secara individual ke serviceApi.
         */
        return serviceApiStok.updateStok(
            idBunga = idBunga,
            jumlah = jumlah,
            tipe = tipe
        )
    }

    override suspend fun getLogStok(idBunga: Int): List<LogStok> =
        serviceApiStok.getLogStok(idBunga)
}