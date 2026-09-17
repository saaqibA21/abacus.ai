package com.abacus.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abacus.app.ui.abacus.AbacusScreenContent
import com.abacus.app.ui.learn.LearnScreenContent
import com.abacus.app.ui.theme.*

sealed class Screen(val route: String, val label: String) {
    object Abacus : Screen("abacus", "Abacus")
    object Learn  : Screen("learn",  "Learn")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbacusApp() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Abacus, Screen.Learn)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            Screen.Learn.route -> "📚 Learn Abacus"
                            else               -> "🧮 Abacus Calculator"
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WoodBrown,
                    titleContentColor = TextLight
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = WoodBrown) {
                screens.forEach { screen ->
                    val icon = if (screen == Screen.Abacus) Icons.Default.Calculate
                               else Icons.Default.School
                    val isSelected = currentRoute == screen.route

                    NavigationBarItem(
                        icon  = { Icon(icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState     = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = HeavenBead,
                            selectedTextColor   = HeavenBead,
                            indicatorColor      = WoodDark,
                            unselectedIconColor = TextLight.copy(alpha = 0.65f),
                            unselectedTextColor = TextLight.copy(alpha = 0.65f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Abacus.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Abacus.route) { AbacusScreenContent() }
            composable(Screen.Learn.route)  { LearnScreenContent() }
        }
    }
}
