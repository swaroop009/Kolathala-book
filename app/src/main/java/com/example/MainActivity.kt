package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.TailorRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TailorViewModel
import com.example.ui.viewmodel.TailorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-edge support
        enableEdgeToEdge()

        // Room DB & ViewModel init
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TailorRepository(database.tailorDao())
        val viewModelFactory = TailorViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[TailorViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        // 1. Login Screen
                        composable("login") {
                            LoginScreen(
                                viewModel = viewModel,
                                onNavigateToDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToSignUp = {
                                    navController.navigate("signup")
                                }
                            )
                        }

                        // 2. Sign Up Screen
                        composable("signup") {
                            SignUpScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 3. Main Dashboard Screen
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToAddOrder = {
                                    navController.navigate("add_order")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile")
                                },
                                onNavigateToOrderDetail = { orderNo ->
                                    navController.navigate("order_detail/$orderNo")
                                }
                            )
                        }

                        // 4. Profile Screen
                        composable("profile") {
                            ProfileScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onLogoutSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 5. Add New Order Form
                        composable("add_order") {
                            AddOrderScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("add_order") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 6. View Customer Details Screen
                        composable(
                            route = "order_detail/{orderNumber}",
                            arguments = listOf(navArgument("orderNumber") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: ""
                            OrderDetailScreen(
                                orderNumber = orderNumber,
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
