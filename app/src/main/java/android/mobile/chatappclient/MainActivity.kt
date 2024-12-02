package android.mobile.chatappclient

import android.mobile.chatappclient.ui.components.MyAppBar
import android.mobile.chatappclient.ui.screens.HomeChatApp
import android.mobile.chatappclient.ui.screens.LoginScreen
import android.mobile.chatappclient.ui.screens.MyProfileScreen
import android.mobile.chatappclient.ui.screens.SearchUser
import android.mobile.chatappclient.ui.screens.UserChat
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
import android.mobile.chatappclient.ui.theme.ChatAppClientTheme
import android.mobile.chatappclient.viewmodels.AuthViewModel
import android.mobile.chatappclient.viewmodels.UserViewModel
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
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDrawerNavigation()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppDrawerNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    //an topbar
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val routesToHideTopBar = listOf(
        "chat/{currentUserId}/{selectedUserId}/{nameUserId}",
        "search",
        "login"
    )
    val showTopBar = currentBackStackEntry?.destination?.route !in routesToHideTopBar

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .background(Color(0xFFE7F5DC))
            ) {
                MyDrawerContent(
                    onHomeClick = {
                        navController.navigate("home") {
                            popUpTo("home") {
                                inclusive = true
                            }
                        }
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    onProfileClick = {
                        navController.navigate("profile") {
                            popUpTo("profile") {
                                inclusive = true
                            }
                        }
                        coroutineScope.launch {
                            drawerState.close()
                        }

                    },
                    onLogoutClick = {
                        navController.navigate("login") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    MyAppBar(
                        title = "USER",
                        icon = painterResource(id = R.drawable.menu),
                        onNavigationClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            AnimatedNavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(
                    route = "home",
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) {
                    HomeChatApp(navController)
                }
                composable(
                    "chat/{currentUserId}/{selectedUserId}/{nameUserId}",
                    enterTransition = { slideInVertically(initialOffsetY = { it }) },
                    exitTransition = { slideOutVertically(targetOffsetY = { it }) }
                ) { navBackStackEntry ->

                    val currentUserId =
                        navBackStackEntry.arguments?.getString("currentUserId") ?: ""
                    val selectedUserId =
                        navBackStackEntry.arguments?.getString("selectedUserId") ?: ""
                    val nameUserId = navBackStackEntry.arguments?.getString("nameUserId") ?: ""

                    UserChat(
                        currentUserId,
                        selectedUserId,
                        nameUserId,
                        navController = navController
                    )
                }
                composable("profile") { MyProfileScreen(userViewModel = UserViewModel()) }
                composable("search"){ SearchUser(userViewModel = UserViewModel(),navController = navController) }
                composable("login"){ LoginScreen(authViewModel = AuthViewModel()) }
            }
        }
    }
}

@Composable
fun MyDrawerContent(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.comment),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally),
            tint = Color(0xff728156)
        )
        Spacer(modifier = Modifier.height(26.dp))
        Divider(color = Color.White, thickness = 1.dp)
        Spacer(modifier = Modifier.height(26.dp))
        DrawerIcon(
            icon = painterResource(id = R.drawable.home),
            label = "Home",
            onClick = onHomeClick
        )
        Spacer(modifier = Modifier.height(26.dp))
        DrawerIcon(
            icon = painterResource(id = R.drawable.user),
            label = "Profile",
            onClick = onProfileClick
        )
        Spacer(modifier = Modifier.weight(1f))
        DrawerIcon(
            icon = painterResource(id = R.drawable.logout),
            label = "LogOut",
            onClick = onLogoutClick
        )
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun DrawerIcon(
    icon: Painter,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
       verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = "",
            modifier = Modifier.size(30.dp),
            tint = Color(0xffB6C99B),
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(text = label, color = Color(0xff728156))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}