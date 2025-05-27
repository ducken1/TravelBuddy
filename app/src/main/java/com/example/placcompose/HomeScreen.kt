package com.example.placcompose

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.UsersData
import com.example.placcompose.ui.theme.BiggerCard
import com.example.placcompose.ui.theme.UsersSeznam
import com.example.placcompose.ui.theme.getProfilePictureUrl
import com.example.placcompose.ui.theme.getUsername
import com.example.placcompose.viewmodel.UsersViewModel
import com.google.firebase.auth.FirebaseAuth
import java.net.URLDecoder
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, openDrawer: () -> Unit)  {

    val auth = FirebaseAuth.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var authState by remember { mutableStateOf(currentUser != null) }

    var selectedItem by remember { mutableStateOf<UsersData?>(null) }
    val usersViewModel: UsersViewModel = viewModel()

    var isBiggerCardVisible by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val searchData: List<UsersData> by usersViewModel.MyData2

    val context = LocalContext.current

    LaunchedEffect(authState) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            authState = firebaseAuth.currentUser != null
        }
        auth.addAuthStateListener(authStateListener)
    }

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
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Menu",
                    tint = Color(0xFFBA6565),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            openDrawer()
                        }
                )

                // Zaobljen TextField v istem slogu kot login
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        usersViewModel.setSearchQuery(it.lowercase())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 16.dp)
                        .offset(y = (-5).dp),
                    placeholder = { Text(text = "Išči po ključnih besedah...", fontSize = 12.sp) },
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
                        focusedBorderColor = Color(0xFFBA6565),
                        unfocusedBorderColor = Color(0xFFBA6565),
                        cursorColor = Color(0xFFBA6565),
                        focusedTrailingIconColor = Color(0xFFBA6565),
                        unfocusedTrailingIconColor = Color(0xFFBA6565)
                    )
                )
            }

            //CHAT
            UsersSeznam(
                usersData = searchData,
                onItemClick = { selectedUserData ->

                    if (authState) {
                        val encodedId = Uri.encode(selectedUserData.id)
                        val encodedName = Uri.encode(selectedUserData.name)
                        val encodedPic =
                            Uri.encode(selectedUserData.profilepicture) // ✅ samo enkrat kodiraj

                        navController.navigate("chat/$encodedId/$encodedName/$encodedPic")
                    }
                    else {
                        Toast.makeText(context, "Za klepet je potrebna prijava", Toast.LENGTH_SHORT).show()
                    }
                },
                navController = navController
            )
        }
    }

// Kartica z večjim prikazom (če je aktivna)
    if (isBiggerCardVisible) {
        BiggerCard(userData = selectedItem!!)
    }
}





