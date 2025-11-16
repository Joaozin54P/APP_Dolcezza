package com.example.dolcezza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background // <-- Novo import necessário
import androidx.compose.foundation.layout.Box // <-- Novo import necessário
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dolcezza.ui.theme.DolcezzaTheme

// ⚠️ Se BackgroundBeige não estiver no DolcezzaTheme, defina-a aqui ou importe-a
val BackgroundBeige = Color(0xFFF5EFE9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DolcezzaTheme {

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

                // TELAS SEM NAVBAR
                val hideNavBar = currentRoute in listOf(
                    "login",
                    "register",
                    "forgot_password",
                    "loading",
                    "cart",
                )

                // 1. Envolvemos tudo em um Box com a cor de fundo desejada.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Transparent)
                ) {
                    Scaffold(
                        // 2. O Scaffold mantém-se transparente
                        containerColor = Color.Transparent,
                        // Remove o padding interno padrão (se for o caso)
                        contentWindowInsets = WindowInsets(0.dp),

                        // O restante do Scaffold permanece inalterado
                        bottomBar = {
                            if (!hideNavBar) {
                                BottomNavBar(
                                    navController = navController,
                                    currentRoute = currentRoute
                                )
                            }
                        }
                    ) { innerPadding ->
                        // 3. AppNavigation é renderizado sobre o fundo bege.
                        AppNavigation(
                            navController = navController,
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }
}