package com.example.tokobunga.viewmodel.bunga

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tokobunga.modeldata.Bunga
import com.example.tokobunga.repositori.RepositoryBunga
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface StatusHomeBunga {
    object Loading : StatusHomeBunga
    data class Success(val list: List<Bunga>) : StatusHomeBunga
    object Error : StatusHomeBunga
}

class HomeBungaViewModel(
    private val repositoryBunga: RepositoryBunga
) : ViewModel() {

    private var allBunga: List<Bunga> = emptyList()

    var statusHome: StatusHomeBunga by mutableStateOf(StatusHomeBunga.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    init {
        getBunga()
    }

    fun getBunga() {
        viewModelScope.launch {
            statusHome = StatusHomeBunga.Loading
            try {
                val response = repositoryBunga.getListBunga()

                // SAMA DENGAN KONSEP DETAIL: Bersihkan karakter aneh
                allBunga = response.map { bunga ->
                    bunga.copy(
                        nama_bunga = bunga.nama_bunga?.replace("\\", "")?.replace("\"", "") ?: "",
                        kategori = bunga.kategori?.replace("\\", "")?.replace("\"", "") ?: "",
                        harga = bunga.harga?.replace("\\", "")?.replace("\"", "") ?: "",
                        // Foto diambil apa adanya dari PHP (yang sudah berisi Base URL)
                        foto_bunga = bunga.foto_bunga?.replace("\\", "")?.replace("\"", "") ?: ""
                    )
                }
                filterAndShow()
            } catch (e: Exception) {
                statusHome = StatusHomeBunga.Error
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        filterAndShow()
    }

    private fun filterAndShow() {
        val filteredList = if (searchQuery.isEmpty()) {
            allBunga
        } else {
            allBunga.filter {
                it.nama_bunga?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        statusHome = StatusHomeBunga.Success(filteredList)
    }
}
