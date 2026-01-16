package com.example.tokobunga.apiservice

import com.example.tokobunga.modeldata.Admin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ServiceApiAuth {

    @POST("login.php")
    suspend fun loginAdmin(
        @Body request: Map<String, String>
    ): Response<Admin>
}