// app/src/main/java/com/example/studysmart/MainActivity.kt
package com.example.studysmart

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.studysmart.presentation.auth.LoginScreen
import com.example.studysmart.presentation.task.TaskScreen
import dagger.hilt.android.AndroidEntryPoint
import com.example.studysmart.presentation.planner.TaskCreateEditScreen
import com.example.studysmart.presentation.dashboard.DashboardScreen
import com.example.studysmart.presentation.profile.ProfileScreen
import com.example.studysmart.presentation.nav.StudyApp
import com.example.studysmart.presentation.session.SessionScreen
import com.example.studysmart.presentation.subject.SubjectScreen
import com.example.studysmart.presentation.theme.StudySmartTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudySmartTheme {
 //               SessionScreen()
//                SubjectScreen()
                 StudyApp()
//               TaskScreen()
//               DashboardScreen()
//               TaskCreateEditScreen( onDone = {}, onCancel = {} )
//                ProfileScreen {  }
//                LoginScreen(
//                    onLoggedIn = {
//                        Toast.makeText(this, "Login success ✅", Toast.LENGTH_SHORT).show()
//                    }
//                )
            }
        }
    }
}

