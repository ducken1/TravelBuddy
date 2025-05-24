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
import com.example.placcompose.ui.theme.BiggerCard
import com.example.placcompose.ui.theme.OglasiSeznam
import com.example.placcompose.viewmodel.OglasiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MojiOglasi(navController: NavHostController) {

    val oglasiViewModel: OglasiViewModel = viewModel()
    val oglasiData: List<OglasData> by oglasiViewModel.MyData

    var selectedItem by remember { mutableStateOf<OglasData?>(null) }

    var isBiggerCardVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { isBiggerCardVisible = false }
    ) {
        OglasiSeznam(
            oglasiData = oglasiData,
            onItemClick = {
                selectedItem = it

                isBiggerCardVisible =!isBiggerCardVisible
            },
            navController
        )
    }


        
    if (isBiggerCardVisible) BiggerCard(oglasData = selectedItem!!)
    
}


