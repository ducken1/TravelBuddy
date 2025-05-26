package com.example.placcompose.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.placcompose.HomeScreen
import com.example.placcompose.LoginScreen
import com.example.placcompose.MojiOglasi
import com.example.placcompose.RegisterScreen
import com.example.placcompose.SettingsScreen
import com.example.placcompose.ui.theme.SplashScreen
import java.net.URLDecoder

@Composable
fun NavGraph(navController: NavHostController, openDrawer: () -> Unit) {


    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("home") {
            HomeScreen(navController, openDrawer)
        }
        composable("login") {
            LoginScreen(navController, openDrawer)
        }
        composable("register") {
            RegisterScreen(navController, openDrawer)
        }
        composable("settings") {
            SettingsScreen(navController, openDrawer)
        }

    }
}