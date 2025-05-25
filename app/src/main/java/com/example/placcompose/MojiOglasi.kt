package com.example.placcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.OglasData
import com.example.placcompose.dataclasses.UsersData
import com.example.placcompose.ui.theme.BiggerCard
import com.example.placcompose.ui.theme.UsersSeznam
import com.example.placcompose.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MojiOglasi(navController: NavHostController) {

    val usersViewModel: UsersViewModel = viewModel()
    val usersData: List<UsersData> by usersViewModel.MyData

    var selectedItem by remember { mutableStateOf<UsersData?>(null) }

    var isBiggerCardVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { isBiggerCardVisible = false }
    ) {
        UsersSeznam(
            usersData = usersData,
            onItemClick = {
                selectedItem = it

                isBiggerCardVisible =!isBiggerCardVisible
            },
            navController
        )
    }


        
    if (isBiggerCardVisible) BiggerCard(userData = selectedItem!!)
    
}


