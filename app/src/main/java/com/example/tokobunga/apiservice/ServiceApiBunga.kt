package com.example.tokobunga.apiservice

import com.example.tokobunga.modeldata.Bunga
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ServiceApiBunga {

    @GET("bunga_list.php")
    suspend fun getListBunga(): List<Bunga>

    @GET("bunga_detail.php")
    suspend fun getDetailBunga(
        @Query("id") idBunga: Int
    ): Bunga

    /**
     * PERBAIKAN FINAL: Menggunakan String agar sinkron dengan Repository.
     * RequestBody dihapus karena menyebabkan mismatch tipe data di Android
     * dan masalah tanda kutip ganda di database.
     */
    @Multipart
    @POST("bunga_add.php")
    suspend fun tambahBunga(
        @Part("nama_bunga") nama: String,
        @Part("kategori") kategori: String,
        @Part("harga") harga: String,
        @Part("stok") stok: String,
        @Part foto: MultipartBody.Part
    ): Response<Void>

    /**
     * PERBAIKAN FINAL:
     * - @Query("id") idBunga untuk menangkap $_GET['id'] di PHP.
     * - Tipe data String untuk field teks.
     * - MultipartBody.Part? (nullable) agar foto tidak wajib saat update.
     */
    @Multipart
    @POST("bunga_update.php")
    suspend fun updateBunga(
        @Query("id") idBunga: Int,
        @Part("nama_bunga") nama: String,
        @Part("kategori") kategori: String,
        @Part("harga") harga: String,
        @Part("stok") stok: String,
        @Part foto: MultipartBody.Part?
    ): Response<Void>

    @DELETE("bunga_delete.php")
    suspend fun deleteBunga(
        @Query("id") idBunga: Int
    ): Response<Void>
}