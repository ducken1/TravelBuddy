package com.example.placcompose

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.placcompose.dataclasses.OglasData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, openDrawer: () -> Unit) {

    val context = LocalContext.current

    var text by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var isButtonEnabled by remember { mutableStateOf(true) }

    val pickImage =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            it?.let { uri ->
                imageUri = uri
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF8E3))
            .padding(horizontal = 24.dp),
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
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding()
            )
        }

        Image(
            painter = rememberImagePainter(data = if (imageUri != null) imageUri else R.drawable.autist),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { pickImage.launch("image/*") }
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(text = "Enter your text") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 10.dp, end = 10.dp)
        )

        Button(
            onClick = {
                if (isButtonEnabled) {
                    isButtonEnabled = false
                    dodajOglas(
                        text = text,
                        imageUri = imageUri,
                        navController = navController,
                        context = context,
                        isButtonEnabled = mutableStateOf(isButtonEnabled)
                    )
                }
            },
            modifier = Modifier
                .wrapContentWidth()
                .height(50.dp)
                .padding(top = 10.dp),

            enabled = isButtonEnabled
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Dodaj oglas")
        }
    }
}

private fun dodajOglas(
    text: String,
    imageUri: Uri?,
    navController: NavHostController,
    context: Context,
    isButtonEnabled: MutableState<Boolean>
) {
    if (text.isNotEmpty() && imageUri != null) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.reference.child("Oglasi")
        val storageRef = FirebaseStorage.getInstance().reference.child("Images")

        val oglasId = databaseReference.push().key
        if (oglasId != null) {
            storageRef.child(oglasId).putFile(imageUri!!).addOnSuccessListener { task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                    val imageUrl = url.toString()
                    val oglasData = OglasData(oglasId, userId, text, imageUrl)

                    databaseReference.child(oglasId).setValue(oglasData)

                    Toast.makeText(context, "Oglas uspešno dodan", Toast.LENGTH_SHORT).show()

                    navController.navigate("home")
                }
            }
        }
    } else {
        Toast.makeText(context, "Prosim vnesite text in izberite sliko", Toast.LENGTH_SHORT).show()
    }
    isButtonEnabled.value = true
}