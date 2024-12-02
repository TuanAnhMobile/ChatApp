package android.mobile.chatappclient.ui.screens

import android.content.Intent
import android.mobile.chatappclient.MainActivity
import android.mobile.chatappclient.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.mobile.chatappclient.viewmodels.AuthViewModel
import android.mobile.chatappclient.viewmodels.StatusViewModel
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider

class LoginActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreen(authViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            Icon(
                painter = painterResource(id = R.drawable.comment),
                contentDescription = "",
                tint = Color(0xff728156),
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "Welcome back you've been missed!",
                fontSize = 16.sp,
                color = Color(0xff728156),
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(text = "Email", color = Color(0xff728156)) },
                modifier = Modifier.width(350.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFE7F5DC),
                    focusedBorderColor = Color(0xFF98A77C),
                    unfocusedBorderColor = Color(0xFFB6C99B)
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(text = "Password", color = Color(0xff728156)) },
                modifier = Modifier.width(350.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFE7F5DC),
                    focusedBorderColor = Color(0xFF98A77C),
                    unfocusedBorderColor = Color(0xFFB6C99B)
                )
            )
            TextButton(
                onClick = {
                    val intent = Intent(context, ForgotPassScreen::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 40.dp)
            ) {
                Text(
                    text = "Forgot Password ?",
                    color = Color(0xff728156),
                    fontSize = 16.sp,
                )
            }
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(
                            context,
                            "Vui lòng nhập đầy đủ thông tin",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        authViewModel.loginWithEmail(email, password) { succes, message ->
                            if (succes) {
                                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context,"Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff98A77C)
                )
            )
            {
                Text(
                    text = "Login",
                    color = Color(0xFFE7F5DC),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
            })
            {
                Text(
                    text = "Not a member? Register now",
                    fontSize = 14.sp,
                    color = Color(0xff728156),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview2() {

}