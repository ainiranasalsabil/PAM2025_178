package com.example.tokobunga.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class Admin(
    val status: Boolean,
    val id_admin: String,
    val nama_lengkap: String,
    val email: String
)
