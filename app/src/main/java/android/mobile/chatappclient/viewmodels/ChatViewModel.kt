package android.mobile.chatappclient.viewmodels

import android.mobile.chatappclient.model.Message
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _message = mutableStateListOf<Message>()
    val message: List<Message> = _message

    //load tin nhan
    fun loadMessages(currentUserId: String, selectedUserId: String) {
        val chatId = getChatId(currentUserId, selectedUserId)
        db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("ChatViewModelwwww", "Error fetching messages", exception)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    _message.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            _message.add(message)
                        }
                    }
                }
            }
    }

    fun sendMessage(chatId: String, senderId: String, messageText: String) {
        val message = Message(
            messageId = "",
            senderId = senderId,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )
        db.collection("chats").document(chatId)
            .collection("messages").add(message)
            .addOnFailureListener { Log.e("ChatViewModel", "Failed to send message", it) }
    }

    public fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) "$user1$user2" else "$user2$user1"
    }
}