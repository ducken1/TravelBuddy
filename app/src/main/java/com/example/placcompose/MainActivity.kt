package com.example.placcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

import androidx.navigation.compose.rememberNavController
import com.example.placcompose.ui.theme.NavigationDrawerM3
import com.example.placcompose.ui.theme.PlacComposeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlacComposeTheme {

                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    NavigationDrawerM3(navController)
                }
            }
        }
    }
}







