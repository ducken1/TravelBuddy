package com.example.placcompose.ui.theme

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import com.example.placcompose.dataclasses.OglasData
import com.example.placcompose.R
import com.example.placcompose.dataclasses.UsersData
import com.example.placcompose.viewmodel.UsersViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.URLEncoder

@Composable
fun UsersItem(userData: UsersData, onClick: () -> Unit, navController: NavHostController) {
    val usersViewModel: UsersViewModel = viewModel()
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid
    var showDialog by remember { mutableStateOf(false) }
    val currentBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val currentScreen = currentBackStackEntry?.destination?.route


    Box(
        modifier = Modifier
            .padding(5.dp)
            .border(
                width = 0.6.dp,
                color = Color(0xFFBA6565),
                shape = RoundedCornerShape(16.dp) // Match Card shape
            )
    ) {
    Card(
        modifier = Modifier
            .width(375.dp)
            .wrapContentHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFDF6EC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Image
            val painter = rememberImagePainter(
                data = userData.profilepicture ?: "",
                builder = {
                    crossfade(true)
                    error(R.drawable.autist)
                    placeholder(R.drawable.autist)
                }
            )

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            // User Info Column
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Name, Age, Bio
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = userData.name ?: "Unknown",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        maxLines = 1
                    )

                    userData.age?.let { age ->
                        Text(
                            text = age,
                            fontSize = 14.sp,
                            color = Color(0xFFBA6565)
                        )
                    }

                    userData.bio?.let { bio ->
                        Text(
                            text = bio,
                            fontSize = 12.sp,
                            color = Color(0xFF777777),
                            maxLines = 5,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Right Column: Cities
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    userData.cities?.city1?.let { CityInfoRow(it) }
                    userData.cities?.city2?.let { CityInfoRow(it) }
                    userData.cities?.city3?.let { CityInfoRow(it) }
                }
            }

        }
    }
}
}
