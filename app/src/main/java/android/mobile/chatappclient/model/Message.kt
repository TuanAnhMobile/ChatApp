package android.mobile.chatappclient.model

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)