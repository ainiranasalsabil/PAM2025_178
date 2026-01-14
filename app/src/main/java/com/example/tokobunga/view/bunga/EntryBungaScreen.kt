package com.example.tokobunga.view.bunga

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.tokobunga.viewmodel.bunga.EntryBungaViewModel
import com.example.tokobunga.viewmodel.provide.PenyediaViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryBungaScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryBungaViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // 1. State untuk menampung Uri dan File Foto
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fotoFile by remember { mutableStateOf<File?>(null) }

    // 2. Launcher untuk mengambil gambar dari galeri
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            fotoFile = uriToFile(it, context)
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FloristTopAppBar(
                title = stringResource(R.string.entry_bunga),
                canNavigateBack = true,
                onNavigateBack = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.padding_medium))
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            // Input Nama
            OutlinedTextField(
                value = viewModel.nama,
                onValueChange = viewModel::onNamaChange,
                label = { Text(stringResource(R.string.nama_bunga)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Input Kategori
            DropdownKategori(
                selected = viewModel.kategori,
                onSelected = viewModel::onKategoriChange
            )

            // Input Harga
            OutlinedTextField(
                value = viewModel.harga,
                onValueChange = viewModel::onHargaChange,
                label = { Text(stringResource(R.string.harga)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Input Stok
            OutlinedTextField(
                value = viewModel.stok,
                onValueChange = viewModel::onStokChange,
                label = { Text(stringResource(R.string.stok)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Tombol Pilih Foto
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(if (imageUri == null) "Pilih Foto Bunga" else "Foto Berhasil Dipilih")
            }

            // Menampilkan nama file jika sudah dipilih
            imageUri?.let {
                Text(text = "File: ${fotoFile?.name}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Tombol Simpan
            Button(
                onClick = {
                    if (fotoFile != null) {
                        viewModel.submitBunga(
                            fileFoto = fotoFile!!,
                            onSuccess = {
                                Toast.makeText(context, "Berhasil simpan data", Toast.LENGTH_SHORT).show()
                                navigateBack()
                            },
                            onError = { error ->
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Harap pilih foto terlebih dahulu!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_simpan))
            }
        }
    }
}

// 5. Fungsi Helper untuk mengubah URI menjadi FILE agar bisa dikirim ke PHP
fun uriToFile(uri: Uri, context: Context): File {
    val contentResolver = context.contentResolver
    val myFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
        outputStream.write(buffer, 0, length)
    }
    outputStream.close()
    inputStream?.close()
    return myFile
}