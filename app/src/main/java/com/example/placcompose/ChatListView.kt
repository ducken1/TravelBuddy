package com.example.placcompose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.placcompose.dataclasses.ChatSummary
import com.example.placcompose.viewmodel.ChatListViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Date
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.foundation.background


@Composable
fun ChatListView(navController: NavHostController, viewModel: ChatListViewModel = viewModel()) {
    val chatList by viewModel.chatList.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFFEF8E3)) // nastavi ozadje
    ) {
        items(chatList) { chat ->
            ChatListItem(
                chatSummary = chat,
                // V ChatListView, znotraj items()
                onClick = {
                    val encodedUserName = Uri.encode(chat.userName)
                    val encodedProfilePicUrl = Uri.encode(chat.profilePictureUrl)
                    navController.navigate("chat/${chat.otherUserId}/$encodedUserName/$encodedProfilePicUrl")
                }
            )
            Divider()
        }
    }
}

@Composable
fun ChatListItem(chatSummary: ChatSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chatSummary.profilePictureUrl.takeIf { it.isNotBlank() },
            placeholder = painterResource(id = R.drawable.autist),
            error = painterResource(id = R.drawable.autist),
            contentDescription = "Profilna slika",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = chatSummary.userName.ifBlank { chatSummary.otherUserId },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


