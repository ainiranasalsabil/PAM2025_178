package com.example.tokobunga.view.bunga

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tokobunga.R
import com.example.tokobunga.modeldata.Bunga
import com.example.tokobunga.viewmodel.bunga.HomeBungaViewModel
import com.example.tokobunga.viewmodel.bunga.StatusHomeBunga
import com.example.tokobunga.viewmodel.provide.PenyediaViewModel

// =======================================================
// HOME SCREEN
// =======================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBungaScreen(
    onAddClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onLogout: () -> Unit,
    onLaporanClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeBungaViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.getBunga()
    }

    Scaffold(
        topBar = {
            FloristTopAppBar(
                title = "Daftar Bunga",
                onLogoutClick = onLogout,
                onLaporanClick = onLaporanClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Bunga")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // ================= SEARCH =================
            SearchBar(
                query = viewModel.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ================= HOME BANNER =================
            if (viewModel.searchQuery.isEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ================= CONTENT =================
            when (val state = viewModel.statusHome) {
                is StatusHomeBunga.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is StatusHomeBunga.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Gagal memuat data")
                    }
                }

                is StatusHomeBunga.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.list) { bunga ->
                            CardBunga(
                                bunga = bunga,
                                onDetailClick = { onItemClick(bunga.id_bunga) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloristTopAppBar(
    title: String,
    onLogoutClick: () -> Unit,
    onLaporanClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onLaporanClick) {
                Icon(Icons.Default.BarChart, contentDescription = "Laporan")
            }

            AssistChip(
                onClick = onLogoutClick,
                label = {
                    Text(
                        "Logout",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                shape = RoundedCornerShape(50),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}



// =======================================================
// SEARCH BAR
// =======================================================

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 32.dp,
                            top = 6.dp,     // ⬅️ TEKS NAIK
                            bottom = 6.dp
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Cari bunga",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}





// =======================================================
// CARD BUNGA
// =======================================================

@Composable
fun CardBunga(
    bunga: Bunga,
    onDetailClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {

            // FOTO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = bunga.foto_bunga,
                    contentDescription = bunga.nama_bunga,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // INFO
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // NAMA
                Text(
                    text = bunga.nama_bunga ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                // KATEGORI (PENTING)
                Text(
                    text = bunga.kategori ?: "",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                // HARGA
                Text(
                    text = "Rp ${bunga.harga}",
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onDetailClick,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Detail")
                }
            }
        }
    }
}