package android.mobile.chatappclient.ui.screens

import android.mobile.chatappclient.R
import android.mobile.chatappclient.model.User
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
import android.mobile.chatappclient.viewmodels.StatusViewModel
import android.mobile.chatappclient.viewmodels.UserViewModel
import android.net.Uri
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

class HomeScreen : ComponentActivity() {
    private val statusViewModel: StatusViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        // Cập nhật trạng thái online khi màn hình hiển thị
        statusViewModel.updateUserOnline()
    }

    override fun onStop() {
        super.onStop()
        // Cập nhật trạng thái offline khi màn hình dừng
        statusViewModel.updateUserOffline()
    }

    override fun onDestroy() {
        super.onDestroy()
        statusViewModel.updateUserOffline()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            HomeChatApp(navController)
            statusViewModel.setupPresence()
        }
    }
}

@Composable
fun HomeChatApp(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    statusViewModel: StatusViewModel = viewModel()
    ) {
//hien thi danh sach nguoi dung
    LaunchedEffect(Unit) {
        statusViewModel.updateUserOnline()
        userViewModel.fetchUsers()
    }
    val listUser = userViewModel.users
    val currentUserId = userViewModel.auth.currentUser?.uid ?: ""

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("search")
                },
                containerColor = Color(0xff728156),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Search, contentDescription = "")
            }
        },
        content = { paddingValues ->
            if (listUser.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Loading.....")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xffEFEFFD))
                ) {
                    Text(
                        text = "Khám phá người dùng mới",
                        color = Color(0xff728156),
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    LazyColumn {
                        items(listUser) { dataUser ->
                            CardUser(user = dataUser, onClick = {
                                //id nguoi duoc chon
                                    selectedUserId ->
                                Log.d("UserChat", "Selected User ID: $selectedUserId")
                                navController.navigate(
                                    "chat/$currentUserId/$selectedUserId/${
                                        Uri.encode(
                                            dataUser.name
                                        )
                                    }"
                                )
                            })
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CardUser(user: User, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(8.dp)
            .clickable {
                onClick(user.userId) // Truyền id người dùng
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)  // Đảm bảo kích thước của Box là 60.dp x 60.dp để tạo ra hình tròn
                    .clip(CircleShape)
                    .background(Color.Gray)  // Màu nền của Box nếu ảnh không có
            ) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "",
                    error = painterResource(id = R.drawable.user_home),
                    placeholder = painterResource(id = R.drawable.user_home),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()  // Sử dụng fillMaxSize để ảnh chiếm hết Box
                        .clip(CircleShape)  // Cắt ảnh thành hình tròn
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = user.name, color = Color(0xff728156))
                Text(
                    text = if (user.status) "Online" else "Offline",
                    color = if (user.status) Color.Green else Color.Red
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview4() {

}