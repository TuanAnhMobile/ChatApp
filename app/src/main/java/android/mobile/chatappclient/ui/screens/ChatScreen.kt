package android.mobile.chatappclient.ui.screens

import android.content.Intent
import android.mobile.chatappclient.MainActivity
import android.mobile.chatappclient.R
import android.mobile.chatappclient.model.Message
import android.mobile.chatappclient.ui.components.MyAppBar
import android.mobile.chatappclient.viewmodels.ChatViewModel
import android.view.WindowInsets
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChat(
    currentUserId: String,
    selectedUserId: String,
    nameUserId: String,
    chatViewModel: ChatViewModel = viewModel(),
    navController: NavController
) {
    chatViewModel.loadMessages(currentUserId, selectedUserId)
    val messages = chatViewModel.message
    var textContent by remember { mutableStateOf("") }
    val context = LocalContext.current


    Scaffold(
        topBar = {
            MyAppBar(
                title = nameUserId,
                icon = painterResource(id = R.drawable.back),
                onNavigationClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE0E0E0))
                .padding(innerPadding)
                .imePadding()
        ) {
            // Danh sách tin nhắn
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Chiếm không gian còn lại
                    .padding(horizontal = 16.dp, vertical = 8.dp),


            ) {
                items(messages) { message ->
                    ChatBundle(message, isCurrentUser = message.senderId == currentUserId)
                }
            }
            // Phần nhập tin nhắn
            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .imePadding() // Đảm bảo không bị che bởi bàn phím
//                    .padding(8.dp)

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    placeholder = { Text("Nhập tin nhắn") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(56.dp)
                    ,
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White
                    )
                )
                IconButton(
                    onClick = {
                        if (textContent.isNotBlank()) {
                            chatViewModel.sendMessage(
                                chatId = chatViewModel.getChatId(currentUserId, selectedUserId),
                                senderId = currentUserId,
                                messageText = textContent
                            )
                            textContent = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Gửi",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBundle(msg: Message, isCurrentUser: Boolean) {
    val backgroundColor = if (isCurrentUser) Color(0xFF4CAF50) else Color(0xFFEEEEEE)
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        contentAlignment = alignment,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = msg.message,
            color = textColor,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .widthIn(max = 250.dp)
        )
    }

}

