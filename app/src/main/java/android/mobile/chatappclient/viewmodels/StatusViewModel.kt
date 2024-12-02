package android.mobile.chatappclient.viewmodels

import android.mobile.chatappclient.model.User
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class StatusViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun updateUserOnline() {
        val currentUserId = auth.currentUser?.uid ?: ""
        val userRef = db.collection("users").document(currentUserId)

        userRef.update("status", true)
            .addOnSuccessListener {
                Log.d("StatusUpdate", "User is now online")
                // Đọc lại dữ liệu sau khi cập nhật
                userRef.get().addOnSuccessListener { document ->
                    val updatedUser = document.toObject(User::class.java)
                    Log.d("StatusUpdate", "User status: ${updatedUser?.status}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("StatusUpdate", "Failed to update status: ${e.message}")
            }
    }

    fun updateUserOffline() {
        val currentUserId = auth.currentUser?.uid ?: ""
        val userRef = db.collection("users").document(currentUserId)

        userRef.update("status", false)
            .addOnSuccessListener {
                Log.d("StatusUpdate", "User is now offline")
                // Đọc lại dữ liệu sau khi cập nhật
                userRef.get().addOnSuccessListener { document ->
                    val updatedUser = document.toObject(User::class.java)
                    Log.d("StatusUpdate", "User status: ${updatedUser?.status}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("StatusUpdate", "Failed to update status: ${e.message}")
            }
    }

    fun setupPresence() {
        val currentUserId = auth.currentUser?.uid ?: return
        // Sử dụng Realtime Database cho trạng thái kết nối
        val userStatusRef = FirebaseDatabase.getInstance().getReference("status/$currentUserId")
        userStatusRef.onDisconnect().setValue(false) // Đặt offline khi mất kết nối
        userStatusRef.setValue(true) // Đặt online khi kết nối
    }
}