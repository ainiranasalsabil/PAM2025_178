package com.example.tokobunga.apiservice

import com.example.tokobunga.modeldata.LogStok
import retrofit2.Response
import retrofit2.http.*

interface ServiceApiStok {

    @FormUrlEncoded // Tambahkan ini
    @POST("stok_update.php")
    suspend fun updateStok(
        @Field("id_bunga") idBunga: Int,   // Gunakan Field, bukan Body Map
        @Field("jumlah") jumlah: Int,
        @Field("tipe") tipe: String
    ): Response<Void>

    @GET("log_stok_list.php")
    suspend fun getLogStok(
        @Query("id_bunga") idBunga: Int
    ): List<LogStok>
}
