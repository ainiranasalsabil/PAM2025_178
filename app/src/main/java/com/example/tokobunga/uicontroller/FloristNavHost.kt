package com.example.tokobunga.uicontroller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tokobunga.uicontroller.route.*
import com.example.tokobunga.view.auth.LoginScreen
import com.example.tokobunga.view.bunga.*
import com.example.tokobunga.view.stok.StokScreen
import com.example.tokobunga.view.laporan.LaporanStokScreen

@Composable
fun FloristNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = DestinasiLogin.route,
        modifier = modifier
    ) {

        // ================= LOGIN =================
        composable(DestinasiLogin.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(DestinasiHome.route) {
                        popUpTo(DestinasiLogin.route) { inclusive = true }
                    }
                }
            )
        }

        // ================= HOME =================
        composable(DestinasiHome.route) {
            HomeBungaScreen(
                onAddClick = {
                    navController.navigate(DestinasiEntryBunga.route)
                },
                onItemClick = { id ->
                    navController.navigate("${DestinasiDetailBunga.route}/$id")
                },
                onLaporanClick = {
                    navController.navigate(DestinasiLaporanStok.route)
                },
                onLogout = {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(DestinasiHome.route) { inclusive = true }
                    }
                }
            )
        }

        // ================= ENTRY =================
        composable(DestinasiEntryBunga.route) {
            EntryBungaScreen(
                navigateBack = { navController.navigateUp() }
            )
        }

        // ================= DETAIL =================
        composable(
            route = DestinasiDetailBunga.routeWithArg,
            arguments = listOf(
                navArgument(DestinasiDetailBunga.ID_BUNGA) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            // 1. Ambil ID dari navigasi
            val id = backStackEntry.arguments?.getInt(DestinasiDetailBunga.ID_BUNGA) ?: 0

            DetailBungaScreen(
                idBunga = id, // <--- TAMBAHKAN INI (Ini yang menyebabkan error merah)
                navigateBack = { navController.navigateUp() },
                navigateToEdit = { idDariScreen ->
                    navController.navigate("${DestinasiEditBunga.route}/$idDariScreen")
                },
                navigateToStok = { idDariScreen ->
                    navController.navigate("${DestinasiStok.route}/$idDariScreen")
                }
            )
        }
        // ================= EDIT =================
        // ================= EDIT =================
        composable(
            route = DestinasiEditBunga.routeWithArg,
            arguments = listOf(
                navArgument(DestinasiEditBunga.ID_BUNGA) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            // 1. Ambil ID dari navigasi
            val id = backStackEntry.arguments?.getInt(DestinasiEditBunga.ID_BUNGA) ?: 0

            EditBungaScreen(
                idBunga = id, // <--- TAMBAHKAN INI agar error "No value passed" hilang
                navigateBack = { navController.popBackStack() }
            )
        }
        // ================= STOK =================
        composable(
            route = DestinasiStok.routeWithArg,
            arguments = listOf(
                navArgument(DestinasiStok.ID_BUNGA) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val idBunga =
                backStackEntry.arguments?.getInt(DestinasiStok.ID_BUNGA) ?: 0

            StokScreen(
                idBunga = idBunga,
                navigateBack = { navController.navigateUp() }
            )
        }

        // ================= LAPORAN =================
        composable(DestinasiLaporanStok.route) {
            LaporanStokScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}
