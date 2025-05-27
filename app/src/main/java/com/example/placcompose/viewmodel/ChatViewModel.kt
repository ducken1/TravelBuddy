import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.placcompose.dataclasses.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private var currentListener: ValueEventListener? = null
    private var currentChatRef: DatabaseReference? = null

    fun generateChatId(user1Id: String, user2Id: String): String {
        return listOf(user1Id, user2Id).sorted().joinToString("_")
    }

    fun sendMessage(text: String, receiverId: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val senderId = user.uid

        val timestamp = System.currentTimeMillis()
        val newMessage = ChatMessage(
            id = timestamp.toString(),
            text = text,
            senderId = senderId,
            receiverId = receiverId,
            timestamp = timestamp
        )

        val chatId = generateChatId(senderId, receiverId)
        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(chatId)
            .child("messages")
            .child(newMessage.id)

        databaseRef.setValue(newMessage)
            .addOnSuccessListener {
                Log.d("Chat", "Sporočilo uspešno poslano v Realtime Database")
                // Brez dodajanja v lokalno stanje, ker listener že posodobi
            }
            .addOnFailureListener { e ->
                Log.e("Chat", "Napaka pri pošiljanju sporočila: ", e)
            }
    }

    fun listenForMessages(receiverId: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val senderId = user.uid
        val chatId = generateChatId(senderId, receiverId)

        // Počisti sporočila (resetiraj stanje)
        _messages.value = emptyList()

        // Odstrani prejšnji listener, če obstaja
        currentListener?.let { listener ->
            currentChatRef?.removeEventListener(listener)
        }

        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(chatId)
            .child("messages")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesList = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                    .sortedByDescending { it.timestamp }
                _messages.value = messagesList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Chat", "Napaka pri poslušanju sporočil", error.toException())
            }
        }

        currentListener = listener
        currentChatRef = databaseRef

        databaseRef.orderByChild("timestamp").addValueEventListener(listener)
    }

    override fun onCleared() {
        // Ko ViewModel ni več v uporabi, počistimo listener
        currentListener?.let { listener ->
            currentChatRef?.removeEventListener(listener)
        }
        super.onCleared()
    }
}

