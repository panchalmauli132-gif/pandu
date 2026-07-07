package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode = viewModel.isDarkMode

            MyApplicationTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val currentUser by viewModel.currentUser.collectAsState()
                
                // Track backstack entry for active bottom tab selection
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Gatekeeping route: Redirect to login if user is null
                LaunchedEffect(currentUser) {
                    if (currentUser == null) {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    } else if (currentRoute == "login" || currentRoute == null) {
                        navController.navigate("dashboard") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                // Show top-level scaffold only if the user is authenticated
                if (currentUser != null) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = when(currentRoute) {
                                            "dashboard" -> "Live Hub"
                                            "customers" -> "Customers"
                                            "products" -> "Catalog"
                                            "cart" -> "Shopping Cart"
                                            "orders" -> "Order Ledger"
                                            "payments" -> "Payments Ledger"
                                            "reports" -> "Performance Analytics"
                                            "admin" -> "Administration"
                                            "profile" -> "My Profile"
                                            else -> "Order Collection"
                                        }
                                    )
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                                    actionIconContentColor = MaterialTheme.colorScheme.primary
                                ),
                                actions = {
                                    // Dropdown or action indicators for quick sub-panels
                                    var showMenu by remember { mutableStateOf(false) }
                                    IconButton(onClick = { showMenu = !showMenu }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                                    }
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Profile Preferences", color = MaterialTheme.colorScheme.onSurface) },
                                            leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            onClick = {
                                                showMenu = false
                                                navController.navigate("profile")
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Reports Sheet", color = MaterialTheme.colorScheme.onSurface) },
                                            leadingIcon = { Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            onClick = {
                                                showMenu = false
                                                navController.navigate("reports")
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Payments Ledger", color = MaterialTheme.colorScheme.onSurface) },
                                            leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            onClick = {
                                                showMenu = false
                                                navController.navigate("payments")
                                            }
                                        )
                                        if (currentUser?.role == "Admin") {
                                            DropdownMenuItem(
                                                text = { Text("Admin Console", color = MaterialTheme.colorScheme.onSurface) },
                                                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                                onClick = {
                                                    showMenu = false
                                                    navController.navigate("admin")
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            // Render sleek M3 navigation bar at the bottom
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                val navTabs = listOf(
                                    NavigationTabItem("dashboard", "Dashboard", Icons.Default.Dashboard),
                                    NavigationTabItem("customers", "Customers", Icons.Default.Group),
                                    NavigationTabItem("products", "Catalog", Icons.Default.Inventory),
                                    NavigationTabItem("cart", "Cart", Icons.Default.ShoppingCart),
                                    NavigationTabItem("orders", "Ledger", Icons.Default.ReceiptLong)
                                )

                                navTabs.forEach { tab ->
                                    val active = currentRoute == tab.route
                                    NavigationBarItem(
                                        selected = active,
                                        label = { Text(tab.title) },
                                        icon = {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.title,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        onClick = {
                                            navController.navigate(tab.route) {
                                                popUpTo("dashboard") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "dashboard",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("dashboard") {
                                DashboardScreen(viewModel = viewModel, onNavigate = { route -> navController.navigate(route) })
                            }
                            composable("customers") {
                                CustomerManagementScreen(viewModel = viewModel)
                            }
                            composable("products") {
                                ProductCatalogScreen(viewModel = viewModel, onNavigateToCart = { navController.navigate("cart") })
                            }
                            composable("cart") {
                                CartScreen(viewModel = viewModel, onOrderPlaced = { navController.navigate("orders") })
                            }
                            composable("orders") {
                                OrderHistoryScreen(viewModel = viewModel)
                            }
                            composable("payments") {
                                PaymentsScreen(viewModel = viewModel)
                            }
                            composable("reports") {
                                ReportsScreen(viewModel = viewModel)
                            }
                            composable("admin") {
                                AdminPanelScreen(viewModel = viewModel)
                            }
                            composable("profile") {
                                ProfileScreen(viewModel = viewModel, onLogout = { navController.navigate("login") })
                            }
                        }
                    }
                } else {
                    // Authenticate if user is currently unauthenticated
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = viewModel,
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavigationTabItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)
