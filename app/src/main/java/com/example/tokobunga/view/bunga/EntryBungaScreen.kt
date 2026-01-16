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

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fotoFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // ====== STATE DIALOG ======
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        uri?.let { fotoFile = uriToFile(it, context) }
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

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ================= DATA BUNGA =================
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

                // ================= FOTO =================
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
                            text = "Foto Bunga",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Button(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (imageUri == null)
                                    "Pilih Foto Bunga"
                                else
                                    "Foto Berhasil Dipilih"
                            )
                        }

                        imageUri?.let {
                            Text(
                                text = "File: ${fotoFile?.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ================= SIMPAN =================
                Button(
                    onClick = {
                        if (fotoFile == null) {
                            dialogTitle = "Peringatan"
                            dialogMessage = "Silakan pilih foto bunga terlebih dahulu."
                            isSuccess = false
                            showDialog = true
                            return@Button
                        }

                        isLoading = true

                        viewModel.submitBunga(
                            fileFoto = fotoFile!!,
                            onSuccess = {
                                isLoading = false
                                dialogTitle = "Berhasil"
                                dialogMessage = "Bunga berhasil ditambahkan."
                                isSuccess = true
                                showDialog = true
                            },
                            onError = { error ->
                                isLoading = false
                                dialogTitle = "Gagal"
                                dialogMessage = error
                                isSuccess = false
                                showDialog = true
                            }
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(160.dp)
                        .height(46.dp),
                    shape = RoundedCornerShape(50),
                    enabled = !isLoading
                ) {
                    Text(stringResource(R.string.btn_simpan))
                }
            }

            // ================= LOADING =================
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // ================= DIALOG =================
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(dialogTitle) },
                    text = { Text(dialogMessage) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                if (isSuccess) navigateBack()
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

// ================= UTIL =================

fun uriToFile(uri: Uri, context: Context): File {
    val contentResolver = context.contentResolver
    val file = File.createTempFile("temp_image", ".jpg", context.cacheDir)
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(file)

    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
        outputStream.write(buffer, 0, length)
    }

    outputStream.close()
    inputStream?.close()
    return file
}