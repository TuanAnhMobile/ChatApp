package android.mobile.chatappclient.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.mobile.chatappclient.model.User
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImagePainter
import com.cloudinary.Cloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val users = mutableStateListOf<User>()

    val currentUserEmail = mutableStateOf("")
    val currentUserName = mutableStateOf("loading....")

    init {
        fetchUserEmail()
        fetchUserName()
    }

    fun fetchUserEmail() {
        val user = auth.currentUser
        if (user != null) {
            currentUserEmail.value = user.email ?: ""
        }
    }

    fun fetchUserName() {
        val currentUserId = auth.currentUser?.uid ?: ""
        db.collection("users")
            .document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("name") ?: ""
                    currentUserName.value = username
                }
            }
            .addOnFailureListener { error ->
                error.printStackTrace()
                Log.e("UserViewModel", "Lỗi khi tải tên người dùng: ${error.message}")
            }
    }

    fun searchUserByName(
        query : String,
        onResult : (List<User>) -> Unit,
        onError : (String) -> Unit,
    ){
        db.collection("users")
            .whereGreaterThanOrEqualTo("name",query)
            .whereLessThanOrEqualTo("name",query + "\uf8ff")
            .get()
            .addOnSuccessListener {
                    documents ->
                val user = documents.map {
                        document ->
                    document.toObject(User::class.java)
                }
                onResult(user)
            }
            .addOnFailureListener {
                    error ->
                onError(error.message ?: "Lỗi không xác định")
            }

    }

    fun UpdateUserName(
        newName: String,
    ) {
        val currentUserId = auth.currentUser?.uid ?: ""
        db.collection("users")
            .document(currentUserId)// Lấy document của người dùng dựa trên UID
            .update("name", newName)// Cập nhật trường "name" trong document của người dùng
            .addOnSuccessListener {
                currentUserName.value = newName // Cập nhật giá trị name trong ViewModel
            }.addOnFailureListener { error ->
                error.printStackTrace()
                Log.e("UserViewModel2222", "Lỗi khi cập nhật tên người dùng: ${error.message}")
            }

    }

    @SuppressLint("Recycle")
    fun uploadPictureUser(
        uri: Uri,
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUserId = auth.currentUser?.uid ?: ""
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val cloudinary = Cloudinary(
                    mapOf(
                        "cloud_name" to "ds9wulr9i",
                        "api_key" to "922321853448379",
                        "api_secret" to "V5dQ69zZNu7N59HF4mBJwBkiMyw"
                    )
                )

                val uploadResult = cloudinary.uploader()
                    .upload(inputStream, mapOf("folder" to "user_profile_pictures"))
                val imageUrl = uploadResult["secure_url"]

                withContext(Dispatchers.Main) {
                    db.collection("users").document(currentUserId)
                        .update("profilePictureUrl", imageUrl)
                        .addOnSuccessListener {
                            onSuccess(imageUrl.toString())
                        }
                        .addOnFailureListener { error ->
                            onError(error)
                        }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    //hien thi user
//    fun fetchUsers() {
//        val currentUserId = auth.currentUser?.uid ?: ""
//        db.collection("users")
//            .whereNotEqualTo("userId", currentUserId)
//            .limit(4)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    Log.e("FETCH_USERS", "Error listening to updates: ${error.message}")
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null) {
//                    users.clear()
//                    for (document in snapshot.documents) {
//                        val user = document.toObject(User::class.java)
//                        if (user != null) {
//                            users.add(user)
//                        }
//                    }
//                }
//            }
//    }

    fun fetchUsers() {
        val currentUserId = auth.currentUser?.uid ?: ""
        db.collection("users")
            .whereNotEqualTo("userId", currentUserId)
//            .limit(4)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FETCH_USERS", "Error listening to updates: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    users.clear()
                    for (document in snapshot.documents) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            Log.d("FETCH_USERS", "User: ${user.name}, Status: ${user.status}")
                            users.add(user)
                        }
                    }
                }
            }
    }

}