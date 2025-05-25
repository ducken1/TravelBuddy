package com.example.placcompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.OglasData
import com.example.placcompose.dataclasses.UsersData
import com.example.placcompose.ui.theme.BiggerCard
import com.example.placcompose.ui.theme.UsersSeznam
import com.example.placcompose.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, openDrawer: () -> Unit)  {



    var selectedItem by remember { mutableStateOf<UsersData?>(null) }
    val usersViewModel: UsersViewModel = viewModel()

    var isBiggerCardVisible by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val searchData: List<UsersData> by usersViewModel.MyData2

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF8E3))
            .clickable { isBiggerCardVisible = false }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEF8E3))
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Menu",
                    tint = Color(0xFFBA6565),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            openDrawer()
                        }
                )

                // Zaobljen TextField v istem slogu kot login
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        usersViewModel.setSearchQuery(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    placeholder = { Text(text = "Išči po ključnih besedah...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search), // če obstaja
                            contentDescription = "Search Icon"
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = Color.Black,
                        textColor = Color.Black
                    )
                )
            }



            Spacer(modifier = Modifier.height(8.dp))

            // Seznam uporabnikov
            UsersSeznam(
                usersData = searchData,
                onItemClick = { selectedUserData ->
                    selectedItem = selectedUserData
                    isBiggerCardVisible = !isBiggerCardVisible
                },
                navController
            )
        }
    }

// Kartica z večjim prikazom (če je aktivna)
    if (isBiggerCardVisible) {
        BiggerCard(userData = selectedItem!!)
    }
}





