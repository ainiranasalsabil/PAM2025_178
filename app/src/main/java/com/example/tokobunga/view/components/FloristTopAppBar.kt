package com.example.tokobunga.view.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloristTopAppBar(
    title: String,
    modifier: Modifier = Modifier,

    // navigasi
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},

    // aksi
    showLogout: Boolean = false,
    onLogoutClick: () -> Unit = {},

    showLaporan: Boolean = false,
    onLaporanClick: () -> Unit = {},

    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,

        // ================= LEFT =================
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
            }
        },

        // ================= RIGHT =================
        actions = {

            // LAPORAN (ICON)
            if (showLaporan) {
                IconButton(onClick = onLaporanClick) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = "Laporan"
                    )
                }
            }

            // LOGOUT (BUTTON)
            if (showLogout) {
                AssistChip(
                    onClick = onLogoutClick,
                    label = {
                        Text(
                            text = "Keluar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp)
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
        }
    )
}