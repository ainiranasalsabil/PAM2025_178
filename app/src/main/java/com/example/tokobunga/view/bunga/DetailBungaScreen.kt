package com.example.tokobunga.view.bunga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBungaScreen(
    idBunga: Int,
    navigateBack: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    navigateToStok: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailBungaViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // ================= NOTIF STATE =================
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val errorMessage = viewModel.errorMessage

    // ================= ERROR DIALOG (GAGAL HAPUS) =================
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text("Gagal Menghapus")
                }
            },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("Tutup")
                }
            }
        )
    }

    // ================= KONFIRMASI HAPUS =================
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Konfirmasi Hapus") },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus bunga ini?\n" +
                            "Bunga yang sudah memiliki riwayat stok tidak dapat dihapus."
                )
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.hapusBunga {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Bunga berhasil dihapus",
                                    duration = SnackbarDuration.Short
                                )
                                navigateBack()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            }
        )
    }

    LaunchedEffect(idBunga) {
        viewModel.loadDetail(idBunga)
    }

    // ================= SCAFFOLD =================
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadDetail(idBunga) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            is StatusDetailBunga.Success -> {
                val bunga = state.bunga

                // ================= UI LAMA (TIDAK DIUBAH) =================
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Card(elevation = CardDefaults.cardElevation(4.dp)) {
                        AsyncImage(
                            model = bunga.foto_bunga,
                            contentDescription = bunga.nama_bunga,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(bunga.nama_bunga, style = MaterialTheme.typography.headlineMedium)
                            Divider(Modifier.padding(vertical = 8.dp))
                            Text("Kategori : ${bunga.kategori}")
                            Text("Harga    : Rp ${bunga.harga}")
                            Text("Stok     : ${bunga.stok}")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { navigateToEdit(bunga.id_bunga) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Edit")
                        }

                        OutlinedButton(
                            onClick = { navigateToStok(bunga.id_bunga) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Stok")
                        }
                    }

                    Button(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
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