package com.example.placcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.OglasData
import com.example.placcompose.ui.theme.BiggerCard
import com.example.placcompose.ui.theme.OglasiSeznam
import com.example.placcompose.viewmodel.OglasiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    val languages = listOf(
        "Java",
        "Kotlin",
        "Python",
        "Dart",
        "PHP",
        "XML",
        "HTML",
        "JavaScript",
        "R",
        "Go",
        "C++",
        "Swift",
        "Verilog"
    )

    var selectedItem by remember { mutableStateOf<OglasData?>(null) }
    val oglasiViewModel: OglasiViewModel = viewModel()

//    val oglasiData: List<OglasData> by oglasiViewModel.AllData

    var isBiggerCardVisible by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val searchData: List<OglasData> by oglasiViewModel.MyData2

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { isBiggerCardVisible = false }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    oglasiViewModel.setSearchQuery(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 6.dp, end = 6.dp),
                placeholder = { Text(text = "Search...") },
                singleLine = true,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                shape = RoundedCornerShape(32.dp)
            )
//            MyApp2(
//                modifier = Modifier,
//                languages
//            )

            OglasiSeznam(
                oglasiData = searchData,
                onItemClick = { selectedOglasData ->
                    selectedItem = selectedOglasData

                    isBiggerCardVisible = !isBiggerCardVisible
                },
                navController
            )
        }
    }
    if (isBiggerCardVisible) {
        BiggerCard(oglasData = selectedItem!!)
    }
}

//@Composable
//fun MyApp2(modifier: Modifier = Modifier, languages: List<String>) {
//    LazyRow() {
//        items(items = languages) { item ->
//            RowItem(modifier = modifier, name = item)
//        }
//    }
//}

//@Composable
//fun RowItem(modifier: Modifier, name: String) {
//    Card(
//        modifier
//            .padding(10.dp)
//            .fillMaxWidth()
//            .height(80.dp)
//            .aspectRatio(1.5f),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.White
//        ),
//        elevation = CardDefaults.cardElevation(10.dp)
//    ) {
//        Box(
//            modifier
//                .padding(10.dp)
//                .fillMaxSize(),
//            contentAlignment = Alignment.Center
//        )
//        {
//            Text(text = name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
//        }
//    }
//}


