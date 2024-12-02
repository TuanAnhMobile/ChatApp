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
import android.mobile.chatappclient.viewmodels.AuthViewModel
import android.mobile.chatappclient.viewmodels.UserViewModel
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext


class ForgotPassScreen : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgotPassWord(authViewModel = authViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassWord(authViewModel: AuthViewModel) {
    var resetEmail by remember { mutableStateOf("") }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7F5DC)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.forgot),
                contentDescription = "",
            )
            OutlinedTextField(
                value = resetEmail,
                onValueChange = { resetEmail = it },
                placeholder = { Text(text = "Enter Your Email", color = Color(0xff728156)) },
                modifier = Modifier.width(350.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFE7F5DC),
                    focusedBorderColor = Color(0xFF98A77C),
                    unfocusedBorderColor = Color(0xFFB6C99B)
                )

            )
            Button(
                onClick = {
                    if (resetEmail.isEmpty()) {
                        Toast.makeText(context, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                    } else {
                        authViewModel.sendResetPasswordEmail(
                            email = resetEmail,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Gửi email thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(context, LoginActivity::class.java)
                                context.startActivity(intent)
                            },
                            onError = {
                                Toast.makeText(
                                    context,
                                    "Vui lòng kiểm tra email của bạn",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff98A77C)

                )
            ) {
                Text(text = "Send")
            }

        }

    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview7() {


}