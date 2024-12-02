package android.mobile.chatappclient.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val status: Boolean = false // Mặc định là false (offline)
)