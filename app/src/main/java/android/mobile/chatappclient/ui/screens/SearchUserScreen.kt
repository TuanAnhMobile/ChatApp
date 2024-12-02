package android.mobile.chatappclient.ui.screens

import android.mobile.chatappclient.R
import android.mobile.chatappclient.model.User
import android.mobile.chatappclient.viewmodels.UserViewModel
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUser(
    userViewModel: UserViewModel,
    navController: NavController
) {
    // Remember trạng thái text
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<User>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    val currentUserId = userViewModel.auth.currentUser?.uid ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffEFEFFD))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Đặt khoảng cách đều giữa các phần tử
        ) {
            // OutlinedTextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFE7F5DC),
                    focusedBorderColor = Color(0xFF98A77C),
                    unfocusedBorderColor = Color(0xFFB6C99B)
                ),
                placeholder = { Text(text = "Enter username") }
            )
            // Button
            IconButton(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        userViewModel.searchUserByName(
                            query = searchQuery,
                            onResult = { users ->
                                searchResults = users
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    } else {
                        errorMessage = "Vui lòng nhập tên người dùng để tìm kiếm"
                    }

                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "",
                    tint = Color(0xff728156)
                )
            }
        }
        LazyColumn {
            items(searchResults) { user ->
                UserItem(user = user, onClick ={
                        selectedUserId ->
                    Log.d("UserSearch", "Selected User ID: $selectedUserId")
                    navController.navigate("chat/$currentUserId/$selectedUserId/${Uri.encode(user.email)}")
                } )
            }
        }
    }
}

@Composable
fun UserItem(user: User, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onClick(user.userId)
            }
    ) {
        AsyncImage(
            model = user.profilePictureUrl,
            contentDescription = "",
            error = painterResource(id = R.drawable.user_home),
            placeholder = painterResource(id = R.drawable.user_home),
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
           
        )

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = user.name)
            Text(text = user.email)
        }

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DefaultPreview() {

}