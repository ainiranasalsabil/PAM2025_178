package com.example.tokobunga.view.bunga

import android.net.Uri
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
import com.example.tokobunga.viewmodel.bunga.EditBungaViewModel
import com.example.tokobunga.viewmodel.provide.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBungaScreen(
    idBunga: Int, // Parameter ID wajib ada
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditBungaViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher untuk mengambil foto baru jika ingin diganti
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            val file = uriToFile(it, context) // Gunakan fungsi helper yang sama seperti di EntryBunga
            viewModel.onFotoSelected(file)
        }
    }

    // 1. Ambil data lama dari server saat layar dibuka
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

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            // Field Nama
            OutlinedTextField(
                value = viewModel.nama,
                onValueChange = viewModel::onNamaChange,
                label = { Text(stringResource(R.string.nama_bunga)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Field Kategori
            DropdownKategori(
                selected = viewModel.kategori,
                onSelected = viewModel::onKategoriChange
            )

            // Field Harga
            OutlinedTextField(
                value = viewModel.harga,
                onValueChange = viewModel::onHargaChange,
                label = { Text(stringResource(R.string.harga)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Field Stok (Penting: PHP Anda mewajibkan stok tidak boleh kosong)
            OutlinedTextField(
                value = viewModel.stok,
                onValueChange = viewModel::onStokChange,
                label = { Text(stringResource(R.string.stok)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Ganti Foto (Opsional)
            OutlinedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (imageUri == null) "Ganti Foto (Opsional)" else "Foto Baru Dipilih")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Update
            Button(
                onClick = {
                    viewModel.updateBunga(
                        onSuccess = {
                            navigateBack()
                        },
                        onError = { error ->
                            // Anda bisa menambahkan Toast di sini
                            println("Error: $error")
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_update))
            }
        }
    }
}