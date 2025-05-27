package com.example.placcompose

import ChatViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.placcompose.dataclasses.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    receiverId: String,
    userName: String,
    profilePicUrl: String,

    openDrawer: () -> Unit
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
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glava z avatarjem in imenom
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
                    .size(40.dp)
                    .clickable { openDrawer() }
            )

            Spacer(modifier = Modifier.width(12.dp))


            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            AsyncImage(
                model = if (isValidImageUrl) profilePicUrl else null,
                placeholder = painterResource(id = R.drawable.avatar),
                error = painterResource(id = R.drawable.avatar),
                fallback = painterResource(id = R.drawable.avatar),
                contentDescription = "Profilna slika",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

        }

        Divider(
            color = Color(0xFFBA6565),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Prikaz sporočil
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            items(messages.asReversed()) { message ->
                ChatBubble(
                    message = message,
                    isCurrentUser = message.senderId == currentUserId
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Vnos in pošiljanje sporočil
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("Vnesi sporočilo...") },
            trailingIcon = {
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
                        contentDescription = "Pošlji",
                        tint = Color(0xFFBA6565)
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFBA6565),
                unfocusedBorderColor = Color(0xFFBA6565),
                cursorColor = Color(0xFFBA6565),
                focusedTrailingIconColor = Color(0xFFBA6565),
                unfocusedTrailingIconColor = Color(0xFFBA6565)
            ),
            shape = RoundedCornerShape(20.dp),
            maxLines = 4
        )

    }
}


@Composable
fun ChatBubble(message: ChatMessage, isCurrentUser: Boolean) {
    val bubbleColor = if (isCurrentUser) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val shape = if (isCurrentUser)
        RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp)
    else
        RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)

    val timeText = remember(message.timestamp) {
        formatTimestamp(message.timestamp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            Column(
                modifier = Modifier
                    .background(color = bubbleColor, shape = shape)
                    .padding(12.dp)
                    .widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Timestamp displayed under the bubble
            Text(
                text = timeText,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

