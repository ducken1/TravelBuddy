package com.example.placcompose.ui.theme

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.OglasData

@Composable
fun OglasiSeznam(
    oglasiData: List<OglasData>,
    onItemClick: (OglasData) -> Unit,
    navController: NavHostController
) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(items = oglasiData) { item ->
            ColumnItem(oglasData = item, onClick = { onItemClick(item) }, navController)
        }
    }
}
