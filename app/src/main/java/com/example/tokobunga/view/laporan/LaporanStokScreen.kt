package com.example.tokobunga.view.laporan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tokobunga.R
import com.example.tokobunga.modeldata.LogStok
import com.example.tokobunga.view.components.FloristTopAppBar
import com.example.tokobunga.viewmodel.laporan.LaporanStokViewModel
import com.example.tokobunga.viewmodel.provide.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanStokScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LaporanStokViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // ================= DATA FILTER =================
    val daftarBulan = listOf(
        "Semua" to 0,
        "Januari" to 1,
        "Februari" to 2,
        "Maret" to 3,
        "April" to 4,
        "Mei" to 5,
        "Juni" to 6,
        "Juli" to 7,
        "Agustus" to 8,
        "September" to 9,
        "Oktober" to 10,
        "November" to 11,
        "Desember" to 12
    )

    val jenisFilter = listOf("Semua", "Masuk", "Keluar")

    // ================= STATE FILTER (UI) =================
    var expandedBulan by remember { mutableStateOf(false) }
    var expandedJenis by remember { mutableStateOf(false) }

    var selectedBulan by remember { mutableStateOf(0) }
    var selectedJenis by remember { mutableStateOf("Semua") }

    // ================= LOAD DATA SAAT FILTER BERUBAH =================
    LaunchedEffect(selectedBulan) {
        viewModel.loadLog(0, selectedBulan)
    }

    // ================= FILTER DATA JENIS =================
    val filteredLog = viewModel.listLog.filter { log ->
        when (selectedJenis) {
            "Masuk" -> log.tipe.lowercase() == "masuk"
            "Keluar" -> log.tipe.lowercase() == "keluar"
            else -> true
        }
    }

    // ================= UI =================
    Scaffold(
        topBar = {
            FloristTopAppBar(
                title = "Laporan Stok",
                canNavigateBack = true,
                onNavigateBack = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {

            // ================= FILTER CARD =================
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ---------- FILTER BULAN ----------
                    ExposedDropdownMenuBox(
                        expanded = expandedBulan,
                        onExpandedChange = { expandedBulan = !expandedBulan }
                    ) {
                        OutlinedTextField(
                            value = daftarBulan.find { it.second == selectedBulan }?.first ?: "Semua",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter Bulan") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedBulan)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedBulan,
                            onDismissRequest = { expandedBulan = false }
                        ) {
                            daftarBulan.forEach { (nama, nilai) ->
                                DropdownMenuItem(
                                    text = { Text(nama) },
                                    onClick = {
                                        selectedBulan = nilai
                                        expandedBulan = false
                                    }
                                )
                            }
                        }
                    }

                    // ---------- FILTER JENIS ----------
                    ExposedDropdownMenuBox(
                        expanded = expandedJenis,
                        onExpandedChange = { expandedJenis = !expandedJenis }
                    ) {
                        OutlinedTextField(
                            value = selectedJenis,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter Jenis Stok") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedJenis)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedJenis,
                            onDismissRequest = { expandedJenis = false }
                        ) {
                            jenisFilter.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        selectedJenis = item
                                        expandedJenis = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= LIST DATA =================
            if (filteredLog.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data sesuai filter")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredLog) { log ->
                        ItemLogStok(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemLogStok(
    log: LogStok,
    modifier: Modifier = Modifier
) {
    val isMasuk = log.tipe.lowercase() == "masuk"
    val warna = if (isMasuk)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.error

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = log.nama_bunga,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            AssistChip(
                onClick = {},
                label = { Text(log.tipe) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = warna.copy(alpha = 0.15f),
                    labelColor = warna
                )
            )

            Text(
                text = "Jumlah : ${log.jumlah}",
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Tanggal : ${log.tanggal}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}