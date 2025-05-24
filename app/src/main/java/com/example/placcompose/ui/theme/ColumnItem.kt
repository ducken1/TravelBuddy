package com.example.placcompose.ui.theme

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import com.example.placcompose.viewmodel.OglasiViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.URLEncoder

@Composable
fun ColumnItem(oglasData: OglasData, onClick: () -> Unit, navController: NavHostController) {

    val oglasiViewModel: OglasiViewModel = viewModel()


    val context = LocalContext.current

    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    var showDialog by remember { mutableStateOf(false) }

    val currentBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val currentScreen = currentBackStackEntry?.destination?.route


    if (showDialog) {

        CustomAlertDialog(
            onYesClick = {
                showDialog = false
                val firebaseRef = FirebaseDatabase.getInstance().getReference("Oglasi")
                val storageRef = FirebaseStorage.getInstance().getReference("Images")
                storageRef.child(oglasData.oglasId.toString()).delete()
                firebaseRef.child(oglasData.oglasId.toString()).removeValue().addOnSuccessListener {

                    navController.navigate("$currentScreen")
                    Toast.makeText(context, "Item removed successfully", Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener { error ->
                        Toast.makeText(context, "error ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            onNoClick = {
                showDialog = false
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            })
    }


    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .aspectRatio(3f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            oglasData.firstImage?.let { imageUrl ->
                val painter: Painter = rememberImagePainter(data = imageUrl)
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(150.dp)
                        .clip(RoundedCornerShape(15.dp))
                )
            }
            oglasData.name?.let {
                Text(
                    text = it,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.Top),
                    maxLines = 1
                )
            }

            if (oglasData.userId == userId) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit), // Use your edit button icon here
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    val oglasId = oglasData.oglasId
                                    val imageUrl = oglasData.firstImage
                                    val name = oglasData.name
                                    Log.d("NavGraph", "Navig/$imageUrl")
                                    val encodedImageUrl = URLEncoder.encode(oglasData.firstImage, "UTF-8")

                                    //navController.navigate("UrediOglas/$imageUrl/$name")
                                    navController.navigate("UrediOglas?image=$encodedImageUrl&name=$name&oglasid=$oglasId")
                                           },
                            tint = Color(0, 180, 0)

                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.remove), // Use your remove button icon here
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    showDialog = true
                                },
                            tint = Color.Red
                        )
                    }
                }
            }


        }
    }
}