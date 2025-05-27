package com.example.placcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.placcompose.dataclasses.ChatSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.database.GenericTypeIndicator


class ChatListViewModel : ViewModel() {
    private val _chatList = MutableStateFlow<List<ChatSummary>>(emptyList())
    val chatList: StateFlow<List<ChatSummary>> = _chatList

    private val databaseRef = FirebaseDatabase.getInstance().reference.child("Chats")
    private val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

    init {
        loadUserChats()
    }

    fun deleteChat(chatId: String) {
        databaseRef.child(chatId).removeValue()
            .addOnSuccessListener {
                // Uspešno izbrisano
            }
            .addOnFailureListener { e ->
                Log.e("ChatListViewModel", "Napaka pri brisanju klepeta: $chatId", e)
            }
    }

    private fun loadUserChats() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatIds = mutableListOf<String>()
                val otherUserIds = mutableSetOf<String>()

                // Najprej poberemo vse klepete, ki vključujejo trenutnega uporabnika
                for (chatSnapshot in snapshot.children) {
                    val chatId = chatSnapshot.key ?: continue
                    if (chatId.contains(currentUserId)) {
                        chatIds.add(chatId)
                        val participants = chatId.split("_")
                        val otherUserId = participants.first { it != currentUserId }
                        otherUserIds.add(otherUserId)
                    }
                }

                // Če ni klepetov, nastavimo prazen seznam
                if (chatIds.isEmpty()) {
                    _chatList.value = emptyList()
                    return
                }

                // Poberemo podatke vseh drugih uporabnikov hkrati (lahko več poizvedb, ker Firebase nima batch get)
                // Toda tukaj naredimo en poizvedbeni klic na "Users" za vse uporabnike po vrsti
                // Ker Firebase Realtime Database nima direktnega batch get, to simuliramo s paralelnimi klici in sledimo ko so vsi končani
                val chatSummaries = mutableListOf<ChatSummary>()
                val remaining = otherUserIds.size
                var completed = 0

                // Ker je asinhrono, uporabimo CountDown-like števec
                otherUserIds.forEach { otherUserId ->
                    usersRef.child(otherUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            val userName = userSnapshot.child("name").getValue(String::class.java) ?: otherUserId
                            val profilePictureUrl = userSnapshot.child("profilepicture").getValue(String::class.java) ?: ""

                            // Za vsak klepet, kjer je ta uporabnik, naredi zapis
                            chatIds.filter { it.contains(otherUserId) }.forEach { chatId ->
                                chatSummaries.add(ChatSummary(chatId, otherUserId, userName, profilePictureUrl))
                            }

                            completed++
                            if (completed == remaining) {
                                // Ko so vsi uporabniki naloženi, posodobi stanje (sorted po userName)
                                _chatList.value = chatSummaries.sortedBy { it.userName.lowercase() }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("ChatListViewModel", "Napaka pri pridobivanju uporabnika", error.toException())
                            completed++
                            if (completed == remaining) {
                                _chatList.value = chatSummaries.sortedBy { it.userName.lowercase() }
                            }
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListViewModel", "Napaka pri nalaganju klepetov", error.toException())
            }
        })
    }
}
