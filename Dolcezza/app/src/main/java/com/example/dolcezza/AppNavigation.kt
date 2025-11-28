package com.example.dolcezza

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dolcezza.ui.screens.AboutUsScreen
import com.example.dolcezza.ui.screens.CartScreen
import com.example.dolcezza.ui.screens.OrderScreen
import com.example.dolcezza.ui.screens.PersonalDataScreen
import com.example.dolcezza.ui.screens.ProfileScreen

@Composable
fun AppNavigation(navController: NavHostController, innerPadding: PaddingValues) {

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(innerPadding)
    ) {

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("explore") {
            ExploreScreen()
        }

        composable("favorites") {
            FavoritesScreen()
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("loading") {
            LoadingScreen(navController = navController)
        }

        composable("personalData") {
            PersonalDataScreen(navController)
        }

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("order") {
            OrderScreen(navController)
        }

        composable("cart") {
            CartScreen(navController)
        }

        composable("aboutUs") {
            AboutUsScreen(navController)
        }

        composable("reviews") {
            ReviewsScreen()
        }

        composable("pieCake") {
            PieCakeScreen()
        }

        composable("cupcakes") {
            CupcakesScreen()
        }

        composable("macarons") {
            MacaronsScreen()
        }

        composable("donuts") {
            DonutsScreen()
        }

        composable("special") {
            SpecialScreen()
        }

        composable("payment") {
            PaymentScreen()
        }

        composable("paymentMethods") {
            paymentMethodsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

    }
}
