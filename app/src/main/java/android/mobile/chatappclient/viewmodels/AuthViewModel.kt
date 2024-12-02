package android.mobile.chatappclient.viewmodels

import android.mobile.chatappclient.model.User
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.lang.Error

class AuthViewModel : ViewModel() {
    //khai bao cac ham dang nhap, dang ky
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var currentUser: FirebaseUser? = auth.currentUser // Lấy thông tin người dùng hiện tại
    //currentUser sẽ tham chiếu đến thông tin người dùng này.
    //cập nhật đúng với trạng thái đăng nhập của Firebase Authentication.

    fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        onComplete: (Boolean, String?) -> Unit
        // Boolean : thanh cong -> true, ko thanh cong -> fale
        // String? : thong bao loi, ko loi se tra ve Null
    ) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = auth.currentUser
                        val newUser = User(
                            userId = user?.uid ?: "",
                            name = name,
                            email = email,
                            profilePictureUrl = "",
                            status = false
                        )
                        //luu thong tin nguoi dung
                        db.collection("users")
                            .document(newUser.userId)
                            .set(newUser)
                            .addOnSuccessListener {
                                currentUser = user
                                onComplete(true, null)
                            }
                            .addOnFailureListener {
                                onComplete(false, it.message)
                            }

                    } else {
                        onComplete(false, it.exception?.message)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ERROR SINGUP", "signUpWithEmail: " + e.message)
                onComplete(false, e.message)
            }
        }
    }

    fun loginWithEmail(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = auth.currentUser
                        currentUser = user
                        onComplete(true, null)
                    } else {
                        onComplete(false, it.exception?.message)
                    }
                }
            } catch (e: Exception) {
                Log.d("ERROR LOGIN", "loginWithEmail: " + e.message)
                onComplete(false, e.message)
            }
        }
    }

    fun sendResetPasswordEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }else{
                    onError(task.exception ?.message ?: "Unknown error")
                }
            }
    }
}