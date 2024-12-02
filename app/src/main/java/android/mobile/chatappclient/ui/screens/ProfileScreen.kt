package android.mobile.chatappclient.ui.screens

import android.content.Context
import android.content.Intent
import android.mobile.chatappclient.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.mobile.chatappclient.ui.screens.ui.theme.ChatAppClientTheme
import android.mobile.chatappclient.viewmodels.UserViewModel
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

class ProfileScreen : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyProfileScreen(userViewModel)
        }
    }
}

@Composable
fun MyProfileScreen(userViewModel: UserViewModel) {
    val email by userViewModel.currentUserEmail
    val userName by userViewModel.currentUserName // Trực tiếp lấy từ ViewModel
    var profilePictureUrl by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val currentUserId = userViewModel.auth.currentUser?.uid ?: return@LaunchedEffect
        userViewModel
            .db
            .collection("users")
            .document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                profilePictureUrl = document.getString("profilePictureUrl") ?: ""
            }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d("MySettings", "URI nhận được: $it") // Thêm log để kiểm tra URI
            userViewModel.uploadPictureUser(
                uri = it,
                context = context,
                onSuccess = { imageUrl ->
                    profilePictureUrl = imageUrl
                },
                onError = { error ->
                    error.printStackTrace()
                    Log.e("MySettings", "Lỗi tải ảnh lên: ${error.message}")
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffEFEFFD)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(26.dp))

        Box(
            modifier = Modifier
                .height(100.dp)
                .background(Color.Gray, shape = CircleShape),
            contentAlignment = Alignment.BottomEnd
        ) {

            if (profilePictureUrl.isNotEmpty()) {
                AsyncImage(
                    model = profilePictureUrl,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.user_home),
                    contentDescription = ""
                )
            }
            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.size(25.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier
                        .background(Color.Gray, shape = CircleShape)
                        .padding(4.dp)
                )

            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        Text(text = email, color = Color(0xff728156))
        Spacer(modifier = Modifier.height(26.dp))
        Text(
            text = "My Detail",
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )
        if (userName == "loading....") {
            CircularProgressIndicator()
        } else {
            DetailProfile("Username", userName, userViewModel)

        }
        Spacer(modifier = Modifier.height(26.dp))
        ChangePassword(context)
    }
}

@Composable
fun DetailProfile(userName: String, name: String, userViewModel: UserViewModel) {

    var showDiaLog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(name) }  // Biến lưu trữ tên người dùng để chỉnh sửa

    if (showDiaLog) {
        DiaLogEditUserName(
            name = newName,
            onNameChange = { newName = it },
            onDismiss = { showDiaLog = false },
            onSave = {
                userViewModel.UpdateUserName(newName)
                showDiaLog = false
            }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(text = userName)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = name)
        }
        IconButton(
            onClick = {
                showDiaLog = true
            }) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "",
                tint = Color(0xff728156)
            )
        }
    }
}

@Composable
fun ChangePassword(context: Context) {
    Button(
        onClick = {
            val intent = Intent (context, ChangePasswordScreen::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(80.dp)
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xff98A77C)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Change Password")
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.Lock, contentDescription = "", tint = Color(0xff728156))
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaLogEditUserName(
    name: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Edit UserName", color = Color(0xff728156)) },
        containerColor = Color(0xffEFEFFD),
        text = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { onNameChange(it) },
                    placeholder = { Text(text = "Enter Your New Name") },
                    modifier = Modifier,
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFE7F5DC),
                        focusedBorderColor = Color(0xFF98A77C),
                        unfocusedBorderColor = Color(0xFFB6C99B)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave()
                    onDismiss()
                }
            ) {
                Text(text = "Save", color = Color(0xff728156))
            }
        })

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview5() {

}