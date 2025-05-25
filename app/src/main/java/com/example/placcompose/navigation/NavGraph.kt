package com.example.placcompose.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.placcompose.DodajOglas
import com.example.placcompose.HomeScreen
import com.example.placcompose.LoginScreen
import com.example.placcompose.MojiOglasi
import com.example.placcompose.RegisterScreen
import com.example.placcompose.SettingsScreen
import com.example.placcompose.UrediOglas
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
        composable("dodajoglas") {
            DodajOglas(navController)
        }
        composable("mojioglasi") {
            MojiOglasi(navController)
        }
        composable("settings") {
            SettingsScreen(navController, openDrawer)
        }
        composable("UrediOglas?image={image}&name={name}&oglasid={oglasid}",
            listOf(
                navArgument(name = "oglasid") {
                    type = NavType.StringType
                },
                navArgument(name = "image") {
                    type = NavType.StringType
                },
                navArgument(name = "name") {
                    type = NavType.StringType
                }
            )
        ) { backstackEntry ->

             val oglasId = backstackEntry.arguments?.getString("oglasid")
            val imageUrl = URLDecoder.decode(
                backstackEntry.arguments?.getString("image"),
                "UTF-8"
            )?.replace("/Images/", "/Images%2F")
            val name = backstackEntry.arguments?.getString("name")

//            val oglasId = backStackEntry.arguments?.getString("oglasId") ?: ""
//            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
//            val name = backStackEntry.arguments?.getString("name") ?: ""
            Log.d("NavigationDebug", "imageUrl: $imageUrl, name: $name")

            if (oglasId != null && imageUrl != null && name != null) {
                UrediOglas(navController,oglasId, imageUrl, name)
            }

        }
    }
}