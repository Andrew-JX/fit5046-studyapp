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
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Android 13+ 通知权限请求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        setContent {
            StudySmartTheme {
                StudyApp()
                // 你可以根据需要切换其他 Screen
                // DashboardScreen()
                // TaskScreen()
                // LoginScreen { ... }
            }
        }
    }
}

