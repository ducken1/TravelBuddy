package com.example.placcompose

import ChatViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.placcompose.dataclasses.ChatMessage
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    receiverId: String,
    userName: String,
    profilePicUrl: String
) {
    val chatViewModel: ChatViewModel = viewModel()
    val messages by chatViewModel.messages.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var message by remember { mutableStateOf("") }

    LaunchedEffect(key1 = receiverId, key2 = currentUserId) {
        chatViewModel.listenForMessages(receiverId)
    }

    // 🪵 Debug: izpiši profilni URL v Logcat
    Log.d("ChatScreen", "Decoded profilePicUrl: $profilePicUrl")

    // 🛡️ Validacija URL-ja
    val isValidImageUrl = remember(profilePicUrl) {
        profilePicUrl.isNotBlank() && (
                profilePicUrl.startsWith("http://") || profilePicUrl.startsWith("https://")
                )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF8E3))
            .padding(16.dp)
    ) {
        // Glava z avatarjem in imenom
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            AsyncImage(
                model = if (isValidImageUrl) profilePicUrl else null,
                placeholder = painterResource(id = R.drawable.autist),
                error = painterResource(id = R.drawable.autist),
                fallback = painterResource(id = R.drawable.autist),
                contentDescription = "Profilna slika",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }

        // Prikaz sporočil
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(
                    message = message,
                    isCurrentUser = message.senderId == currentUserId
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Vnos in pošiljanje sporočil
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Vnesi sporočilo...") }
            )
            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        chatViewModel.sendMessage(message, receiverId)
                        message = ""
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "Pošlji"
                )
            }
        }
    }
}


@Composable
fun ChatBubble(message: ChatMessage, isCurrentUser: Boolean) {
    val bubbleColor = if (isCurrentUser) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val shape = if (isCurrentUser) RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp)
    else RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Box(
            modifier = Modifier
                .background(color = bubbleColor, shape = shape)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
