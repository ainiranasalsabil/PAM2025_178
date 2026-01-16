package com.example.tokobunga.view.bunga

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tokobunga.R
import com.example.tokobunga.view.components.DropdownKategori
import com.example.tokobunga.view.components.FloristTopAppBar
import com.example.tokobunga.viewmodel.bunga.EditBungaViewModel
import com.example.tokobunga.viewmodel.provide.PenyediaViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBungaScreen(
    idBunga: Int,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditBungaViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    // ===== DIALOG STATE =====
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    // ===== FOTO BARU =====
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            val file = uriToFileEdit(it, context)
            viewModel.onFotoSelected(file)
        }
    }

    // ================= LOAD DATA =================
    LaunchedEffect(idBunga) {
        viewModel.loadDataById(idBunga)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FloristTopAppBar(
                title = stringResource(R.string.edit_bunga),
                canNavigateBack = true,
                onNavigateBack = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ================= FORM DATA =================
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = viewModel.nama,
                            onValueChange = viewModel::onNamaChange,
                            label = { Text(stringResource(R.string.nama_bunga)) },
                            enabled = !viewModel.isLocked,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        DropdownKategori(
                            selected = viewModel.kategori,
                            onSelected = viewModel::onKategoriChange
                        )

                        OutlinedTextField(
                            value = viewModel.harga,
                            onValueChange = viewModel::onHargaChange,
                            label = { Text(stringResource(R.string.harga)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            prefix = { Text("Rp ") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // ================= FOTO (OPSIONAL) =================
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "Ganti Foto Bunga (Opsional)",
                            style = MaterialTheme.typography.titleSmall
                        )

                        OutlinedButton(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (imageUri == null)
                                    "Pilih Foto Baru"
                                else
                                    "Foto Baru Dipilih"
                            )
                        }
                    }
                }

                // ================= UPDATE =================
                Button(
                    onClick = {
                        isLoading = true
                        viewModel.updateBunga(
                            onSuccess = {
                                isLoading = false
                                dialogTitle = "Berhasil"
                                dialogMessage = "Data bunga berhasil diperbarui."
                                isSuccess = true
                                showDialog = true
                            },
                            onError = { pesan ->
                                isLoading = false
                                isSuccess = false

                                if (
                                    pesan.contains("stok", true) ||
                                    pesan.contains("riwayat", true)
                                ) {
                                    dialogTitle = "Tidak Dapat Diperbarui"
                                    dialogMessage =
                                        "Data bunga ini tidak dapat diperbarui karena sudah memiliki riwayat stok.\n\n" +
                                                "Perubahan hanya diperbolehkan sebelum ada transaksi stok."
                                } else {
                                    dialogTitle = "Gagal Memperbarui"
                                    dialogMessage = pesan
                                }

                                showDialog = true
                            }
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(170.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(50),
                    enabled = !isLoading
                ) {
                    Text(stringResource(R.string.btn_update))
                }
            }

            // ================= LOADING =================
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // ================= DIALOG =================
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    icon = {
                        if (!isSuccess) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    title = { Text(dialogTitle) },
                    text = { Text(dialogMessage) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                if (isSuccess) navigateBack()
                            }
                        ) {
                            Text("Mengerti")
                        }
                    }
                )
            }
        }
    }
}

/**
 * ================= UTIL KHUSUS EDIT (ANTI CONFLICT)
 */
fun uriToFileEdit(uri: Uri, context: Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File.createTempFile("edit_foto_", ".jpg", context.cacheDir)
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
    return file
}