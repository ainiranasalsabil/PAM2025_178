package com.example.tokobunga.repositori

import com.example.tokobunga.apiservice.ServiceApiBunga
import com.example.tokobunga.modeldata.Bunga
import okhttp3.MultipartBody
import retrofit2.Response

interface RepositoryBunga {

    suspend fun getListBunga(): List<Bunga>
    suspend fun getDetailBunga(id: Int): Bunga

    // PERBAIKAN: Gunakan String untuk field teks
    suspend fun tambahBunga(
        nama: String,
        kategori: String,
        harga: String,
        stok: String,
        foto: MultipartBody.Part
    ): Response<Void>

    // PERBAIKAN: Gunakan String untuk field teks
    suspend fun updateBunga(
        id: Int,
        nama: String,
        kategori: String,
        harga: String,
        stok: String,
        foto: MultipartBody.Part?
    ): Response<Void>

    suspend fun deleteBunga(id: Int): Response<Void>
}

class JaringanRepositoryBunga(
    private val serviceApiBunga: ServiceApiBunga
) : RepositoryBunga {

    override suspend fun getListBunga(): List<Bunga> =
        serviceApiBunga.getListBunga()

    override suspend fun getDetailBunga(id: Int): Bunga =
        serviceApiBunga.getDetailBunga(id)

    // Sesuai dengan interface di atas, gunakan String
    override suspend fun tambahBunga(
        nama: String,
        kategori: String,
        harga: String,
        stok: String,
        foto: MultipartBody.Part
    ): Response<Void> =
        serviceApiBunga.tambahBunga(nama, kategori, harga, stok, foto)

    // Sesuai dengan interface di atas, gunakan String
    override suspend fun updateBunga(
        id: Int,
        nama: String,
        kategori: String,
        harga: String,
        stok: String,
        foto: MultipartBody.Part?
    ): Response<Void> =
        serviceApiBunga.updateBunga(id, nama, kategori, harga, stok, foto)

    override suspend fun deleteBunga(id: Int): Response<Void> =
        serviceApiBunga.deleteBunga(id)
}