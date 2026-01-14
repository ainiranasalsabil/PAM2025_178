package com.example.tokobunga.view.bunga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tokobunga.view.components.FloristTopAppBar
import com.example.tokobunga.viewmodel.bunga.DetailBungaViewModel
import com.example.tokobunga.viewmodel.bunga.StatusDetailBunga
import com.example.tokobunga.viewmodel.provide.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBungaScreen(
    idBunga: Int, // 1. Tambahkan parameter ID di sini
    navigateBack: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    navigateToStok: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailBungaViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // 2. Panggil data dari server saat layar pertama kali muncul
    LaunchedEffect(idBunga) {
        viewModel.loadDetail(idBunga)
    }

    Scaffold(
        topBar = {
            FloristTopAppBar(
                title = "Detail Bunga",
                canNavigateBack = true,
                onNavigateBack = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->

        when (val state = viewModel.statusDetail) {
            is StatusDetailBunga.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is StatusDetailBunga.Error -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gagal memuat detail bunga")
                        Button(onClick = { viewModel.loadDetail(idBunga) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            is StatusDetailBunga.Success -> {
                val bunga = state.bunga

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ================= FOTO =================
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        AsyncImage(
                            model = bunga.foto_bunga,
                            contentDescription = bunga.nama_bunga,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // ================= INFO =================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = bunga.nama_bunga,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Kategori : ${bunga.kategori}", style = MaterialTheme.typography.bodyLarge)
                            Text("Harga    : Rp ${bunga.harga}", style = MaterialTheme.typography.bodyLarge)
                            Text("Stok     : ${bunga.stok}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    HorizontalDivider()

                    // ================= ACTION BUTTONS =================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { navigateToEdit(bunga.id_bunga) },
                            enabled = state.canEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit")
                        }

                        OutlinedButton(
                            onClick = { navigateToStok(bunga.id_bunga) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Stok")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            // Tambahkan konfirmasi hapus jika perlu
                            viewModel.hapusBunga(navigateBack)
                        },
                        enabled = state.canDelete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Hapus Bunga")
                    }
                }
            }
        }
    }
}