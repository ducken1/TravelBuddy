package com.example.placcompose.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.UsersData

@Composable
fun UsersSeznam(
    usersData: List<UsersData>,
    onItemClick: (UsersData) -> Unit,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        usersData.forEach { user ->
            UsersItem(
                userData = user,
                onClick = { onItemClick(user) },
                navController = navController
            )
        }
    }
}
